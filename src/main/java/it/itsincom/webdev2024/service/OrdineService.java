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
import java.util.Calendar;
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


    public List<Ordine> getAllOrders() {
        return orderRepository.getAllOrdini();
    }

    public Ordine createOrderFromRequest(CreateOrderRequest request) {
        // Check if the dataRitiro is in the past
        if (request.getDataRitiro().before(new Date())) {
            throw new IllegalArgumentException("Non è possibile creare un ordine nel passato.");
        }

        // Calculate the 10-minute interval before and after the dataRitiro
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(request.getDataRitiro());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour < 8 || hour >= 19) {
            throw new IllegalArgumentException("Gli ordini possono essere ritirati solo tra le 08:00:00 e le 19:00:00.");
        }
        calendar.add(Calendar.MINUTE, -10);
        Date startTime = calendar.getTime();
        calendar.setTime(request.getDataRitiro());
        calendar.add(Calendar.MINUTE, 10);
        Date endTime = calendar.getTime();

        // Retrieve all orders and filter them within the time range
        List<Ordine> allOrders = orderRepository.getAllOrdini();
        List<Ordine> conflictingOrders = new ArrayList<>();
        for (Ordine ordine : allOrders) {
            if (ordine.getDataRitiro().after(startTime) && ordine.getDataRitiro().before(endTime)) {
                conflictingOrders.add(ordine);
            }
        }

        if (!conflictingOrders.isEmpty()) {
            throw new IllegalStateException("Non è possibile creare un ordine in questo intervallo di tempo.");
        }

        // Create and save the new order
        Ordine ordine = new Ordine();
        ordine.setIdUtente(request.getIdUtente());
        ordine.setDataOrdine(new Date());
        ordine.setDataRitiro(request.getDataRitiro());
        ordine.setStato("in attesa");

        List<Prodotto> prodotti = new ArrayList<>();
        double totale = 0;

        for (ProductOrderRequest productOrder : request.getProdotti()) {
            if (productOrder.getQuantita() == null) {
                throw new IllegalArgumentException("Quantita is required for product: " + productOrder.getNome());
            }
            if (productOrder.getQuantita() <= 0) {
                throw new IllegalArgumentException("Quantita must be greater than 0 for product: " + productOrder.getNome());
            }

            Prodotto prodotto = prodottoRepository.getProdotto(productOrder.getNome());
            if (prodotto == null || prodotto.getQuantita() < productOrder.getQuantita()) {
                throw new RuntimeException("Not enough stock for product: " + productOrder.getNome());
            }

            prodotto.setQuantita(productOrder.getQuantita());
            prodotti.add(prodotto);

            totale += prodotto.getPrezzo() * productOrder.getQuantita();
        }
        totale = Math.round(totale * 100.00) / 100.00;

        ordine.setProdotti(prodotti);
        ordine.setTotale(totale);

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

    public Ordine getOrderById(ObjectId id) {
        return orderRepository.findOrderById(id);
    }

    public List<Ordine> getOrdersByUserId(int userId) {
        return orderRepository.findOrdersByUserId(userId);
    }

    public Ordine acceptOrder(ObjectId orderId) {
        return orderRepository.acceptOrder(orderId);
    }

    public Ordine cancelOrder(ObjectId orderId) {
        return orderRepository.cancelOrder(orderId);
    }


    private String getUserEmailById(int userId) {
        CreateProfileResponse user = utenteService.getUtenteById(userId);
        return user.getEmail();
    }

    public void sendOrderConfirmationEmail(Ordine createdOrder) {
        String email = getUserEmailById(createdOrder.getIdUtente());
        String emailSubject = "Order Confirmation - Order #" + createdOrder.getId();
        StringBuilder emailBody = new StringBuilder();
        emailBody
                .append("<h2>Gentile utente,</h2>")
                .append("<p>Il tuo ordine è appena stato creato.</p>")
                .append("<p><strong>ID ordine:</strong> ")
                .append(createdOrder.getId())
                .append("</p>")
                .append("<p><strong>Totale:</strong> ")
                .append(String.format("%.2f", createdOrder.getTotale()))
                .append("</p>")
                .append("<p><strong>Prodotti:</strong></p>")
                .append("<ul>");
        for (Prodotto prodotto : createdOrder.getProdotti()) {
            emailBody
                    .append("<li>")
                    .append(prodotto.getNome())
                    .append(" | Quantità: ")
                    .append(prodotto.getQuantita())
                    .append(" | Prezzo: ")
                    .append(String.format("%.2f", prodotto.getPrezzo()))
                    .append("</li>");
        }
        emailBody
                .append("</ul>")
                .append("<p>Grazie per aver acquistato da noi!</p>")
                .append("<p>Bacini,</p>")
                .append("<p>XOXO</p>");
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
        emailBody
                .append("<h2>Gentile utente,</h2>")
                .append("<p>Il tuo ordine è stato accettato.</p>")
                .append("<p><strong>ID ordine:</strong> ")
                .append(createdOrder.getId())
                .append("</p>")
                .append("<p><strong>Totale:</strong> ")
                .append(String.format("%.2f", createdOrder.getTotale()))
                .append("</p>").append("<p><strong>Prodotti:</strong></p>")
                .append("<ul>");
        for (Prodotto prodotto : createdOrder.getProdotti()) {
            emailBody
                    .append("<li>")
                    .append(prodotto.getNome())
                    .append(" | Quantità: ")
                    .append(prodotto.getQuantita())
                    .append(" | Prezzo: ")
                    .append(String.format("%.2f", prodotto.getPrezzo()))
                    .append("</li>");
        }
        emailBody.append("</ul>")
                .append("<p>Grazie per aver acquistato da noi!</p>")
                .append("<p>Bacini,</p>")
                .append("<p>XOXO</p>");
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
        emailBody.append("<h2>Gentile utente,</h2>")
                .append("<p>Il tuo ordine è stato rifiutato.</p>")
                .append("<p><strong>ID ordine:</strong> ")
                .append(createdOrder.getId()).append("</p>")
                .append("<p><strong>Totale:</strong> ")
                .append(String.format("%.2f", createdOrder.getTotale()))
                .append("</p>")
                .append("<p><strong>Prodotti:</strong></p>")
                .append("<ul>");
        for (Prodotto prodotto : createdOrder.getProdotti()) {
            emailBody
                    .append("<li>")
                    .append(prodotto.getNome())
                    .append(" | Quantità: ")
                    .append(prodotto.getQuantita())
                    .append(" | Prezzo: ")
                    .append(String.format("%.2f", prodotto.getPrezzo()))
                    .append("</li>");
        }
        emailBody
                .append("</ul>")
                .append("<p>Grazie per aver acquistato da noi!</p>")
                .append("<p>Bacini,</p>")
                .append("<p>XOXO</p>");
        try {
            mailer.send(Mail.withHtml(email, emailSubject, emailBody.toString()));
        } catch (Exception e) {
            System.err.println("Failed to send order confirmation email: " + e.getMessage());
        }
    }
}
