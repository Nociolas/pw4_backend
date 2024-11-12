package it.itsincom.webdev2024.service;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import it.itsincom.webdev2024.persistence.model.Ordine;
import it.itsincom.webdev2024.persistence.model.Prodotto;
import it.itsincom.webdev2024.persistence.repository.OrdineRepository;
import it.itsincom.webdev2024.persistence.repository.ProdottoRepository;
import it.itsincom.webdev2024.rest.model.CreateOrderRequest;
import it.itsincom.webdev2024.rest.model.CreateProfileResponse;
import it.itsincom.webdev2024.rest.model.ProductOrderRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class OrdineService {
    @Inject
    OrdineRepository orderRepository;

    @Inject
    ProdottoRepository prodottoRepository;  // Inject ProdottoRepository

    @Inject
    UtenteService utenteService;

    @Inject
    Mailer mailer;


    // Create a new order

    public Ordine createOrderFromRequest(CreateOrderRequest request) {
        Ordine ordine = new Ordine();
        ordine.setIdUtente(request.getIdUtente());
        ordine.setDataOrdine(new Date());
        ordine.setStato("in attesa");

        List<Prodotto> prodotti = new ArrayList<>();
        double totale = 0;

        for (ProductOrderRequest productOrder : request.getProdotti()) {
            // Validate `quantita` before processing the product
            if (productOrder.getQuantita() == null) {
                throw new IllegalArgumentException("Quantita is required for product: " + productOrder.getNome());
            }
            if (productOrder.getQuantita() <= 0) {
                throw new IllegalArgumentException("Quantita must be greater than 0 for product: " + productOrder.getNome());
            }

            // Retrieve product from repository
            Prodotto prodotto = prodottoRepository.getProdotto(productOrder.getNome());
            if (prodotto == null || prodotto.getQuantita() < productOrder.getQuantita()) {
                throw new RuntimeException("Not enough stock for product: " + productOrder.getNome());
            }

            // Set the requested quantity for the order and add to the product list
            prodotto.setQuantita(productOrder.getQuantita());
            prodotti.add(prodotto);

            // Calculate the total cost for this order
            totale += prodotto.getPrezzo() * productOrder.getQuantita();
        }
        totale = Math.round(totale * 100.0) / 100.0;
        // Set products and total for the order
        ordine.setProdotti(prodotti);
        ordine.setTotale(totale);

        // Save the order and update stock quantities
        ordine = orderRepository.saveOrder(ordine);
        updateStockQuantities(ordine);

        return ordine;
    }


    private void updateStockQuantities(Ordine ordine) {
        for (Prodotto prodotto : ordine.getProdotti()) {
            Prodotto dbProdotto = prodottoRepository.getProdotto(prodotto.getNome());
            if (dbProdotto != null) {
                dbProdotto.setQuantita(dbProdotto.getQuantita() - prodotto.getQuantita());
                prodottoRepository.updateProdotto(dbProdotto);
            }
        }
    }

    // Get order by ID
    public Ordine getOrderById(ObjectId id) {
        return orderRepository.findOrderById(id);
    }

    // Get orders by user ID
    public List<Ordine> getOrdersByUserId(int userId) {
        return orderRepository.findOrdersByUserId(userId);
    }

    // Update order status (e.g., from "in attesa" to "completato")
    public Ordine updateOrderStatus(ObjectId orderId) {
        return orderRepository.updateOrderStatus(orderId);
    }

    // Cancel order (delete it from the database)
    public boolean cancelOrder(ObjectId orderId) {
        return orderRepository.deleteOrder(orderId);
    }


    private String getUserEmailById(int userId) {
        CreateProfileResponse user = utenteService.getUtenteById(userId);
        return user.getEmail();
    }

    public void sendOrderConfirmationEmail(Ordine createdOrder) {
        String email = getUserEmailById(createdOrder.getIdUtente());
        String emailSubject = "Order Confirmation - Order #" + createdOrder.getId();
        StringBuilder emailBody = new StringBuilder();
        emailBody.append("<h2>Gentile utente,</h2>").append("<p>Il tuo ordine è appena stato creato.</p>").append("<p><strong>ID ordine:</strong> ").append(createdOrder.getId()).append("</p>").append("<p><strong>Totale:</strong> ").append(String.format("%.2f", createdOrder.getTotale())).append("</p>").append("<p><strong>Prodotti:</strong></p>").append("<ul>");
        for (Prodotto prodotto : createdOrder.getProdotti()) {
            emailBody.append("<li>").append(prodotto.getNome()).append(" | Quantità: ").append(prodotto.getQuantita()).append(" | Prezzo: ").append(String.format("%.2f", prodotto.getPrezzo())).append("</li>");
        }
        emailBody.append("</ul>").append("<p>Grazie per aver acquistato da noi!</p>").append("<p>Bacini,</p>").append("<p>XOXO</p>");
        try {
            mailer.send(Mail.withHtml(email, emailSubject, emailBody.toString()));
        } catch (Exception e) {
            System.err.println("Failed to send order confirmation email: " + e.getMessage());
        }
    }

    public void sendOrderAcceptedEmail(Ordine createdOrder) {
        String email = getUserEmailById(createdOrder.getIdUtente());
        String emailSubject = "Ordine Accettato- Ordine #" + createdOrder.getId();
        StringBuilder emailBody = new StringBuilder();
        emailBody.append("<h2>Gentile utente,</h2>").append("<p>Il tuo ordine è stato accettato.</p>").append("<p><strong>ID ordine:</strong> ").append(createdOrder.getId()).append("</p>").append("<p><strong>Totale:</strong> ").append(String.format("%.2f", createdOrder.getTotale())).append("</p>").append("<p><strong>Prodotti:</strong></p>").append("<ul>");
        for (Prodotto prodotto : createdOrder.getProdotti()) {
            emailBody.append("<li>").append(prodotto.getNome()).append(" | Quantità: ").append(prodotto.getQuantita()).append(" | Prezzo: ").append(String.format("%.2f", prodotto.getPrezzo())).append("</li>");
        }
        emailBody.append("</ul>").append("<p>Grazie per aver acquistato da noi!</p>").append("<p>Bacini,</p>").append("<p>XOXO</p>");
        try {
            mailer.send(Mail.withHtml(email, emailSubject, emailBody.toString()));
        } catch (Exception e) {
            System.err.println("Failed to send order confirmation email: " + e.getMessage());
        }
    }

    public void sendOrderRejectedEmail(Ordine createdOrder) {
        String email = getUserEmailById(createdOrder.getIdUtente());
        String emailSubject = "Ordine rifiutato - Ordine #" + createdOrder.getId();
        StringBuilder emailBody = new StringBuilder();
        emailBody.append("<h2>Gentile utente,</h2>").append("<p>Il tuo ordine è stato rifiutato.</p>").append("<p><strong>ID ordine:</strong> ").append(createdOrder.getId()).append("</p>").append("<p><strong>Totale:</strong> ").append(String.format("%.2f", createdOrder.getTotale())).append("</p>").append("<p><strong>Prodotti:</strong></p>").append("<ul>");
        for (Prodotto prodotto : createdOrder.getProdotti()) {
            emailBody.append("<li>").append(prodotto.getNome()).append(" | Quantità: ").append(prodotto.getQuantita()).append(" | Prezzo: ").append(String.format("%.2f", prodotto.getPrezzo())).append("</li>");
        }
        emailBody.append("</ul>").append("<p>Grazie per aver acquistato da noi!</p>").append("<p>Bacini,</p>").append("<p>XOXO</p>");
        try {
            mailer.send(Mail.withHtml(email, emailSubject, emailBody.toString()));
        } catch (Exception e) {
            System.err.println("Failed to send order confirmation email: " + e.getMessage());
        }
    }


}
