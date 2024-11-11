package it.itsincom.webdev2024.rest;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import it.itsincom.webdev2024.persistence.model.Ordine;
import it.itsincom.webdev2024.persistence.model.Prodotto;
import it.itsincom.webdev2024.rest.model.CreateOrderRequest;
import it.itsincom.webdev2024.rest.model.CreateProfileResponse;
import it.itsincom.webdev2024.service.OrdineService;
import it.itsincom.webdev2024.service.UtenteService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;

@Path("/api/ordini")
public class OrdineResource {
    @Inject
    OrdineService orderService;

    @Inject
    UtenteService utenteService;

    @Inject
    Mailer mailer;

    // Endpoint to create a new order
    @POST
    public Response createOrder(CreateOrderRequest orderRequest) {
        try {
            Ordine createdOrder = orderService.createOrderFromRequest(orderRequest);

            // Send confirmation email
            sendOrderConfirmationEmail(createdOrder);

            return Response.status(Response.Status.CREATED).entity(createdOrder).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    private void sendOrderConfirmationEmail(Ordine createdOrder) {
        // Get user email
        String email = getUserEmailById(createdOrder.getIdUtente());

        // Create HTML email content
        String emailSubject = "Order Confirmation - Order #" + createdOrder.getId();
        StringBuilder emailBody = new StringBuilder();

        emailBody.append("<h2>Gentile utente,</h2>")
                .append("<p>Il tuo ordine è appena stato creato.</p>")
                .append("<p><strong>ID ordine:</strong> ").append(createdOrder.getId()).append("</p>")
                .append("<p><strong>Totale:</strong> ").append(String.format("%.2f", createdOrder.getTotale())).append("</p>")
                .append("<p><strong>Prodotti:</strong></p>")
                .append("<ul>");

        // Iterate through the products
        for (Prodotto prodotto : createdOrder.getProdotti()) {
            emailBody.append("<li>")
                    .append(prodotto.getNome())
                    .append(" | Quantità: ").append(prodotto.getQuantita())
                    .append(" | Prezzo: ").append(String.format("%.2f", prodotto.getPrezzo()))
                    .append("</li>");
        }

        emailBody.append("</ul>")
                .append("<p>Grazie per aver acquistato da noi!</p>")
                .append("<p>Bacini,</p>")
                .append("<p>XOXO</p>");

        // Send the HTML email
        try {
            mailer.send(Mail.withHtml(email, emailSubject, emailBody.toString()));
        } catch (Exception e) {
            System.err.println("Failed to send order confirmation email: " + e.getMessage());
        }
    }



    private String getUserEmailById(int userId) {
        CreateProfileResponse user = utenteService.getUtenteById(userId);
        return user.getEmail();
    }

    // Endpoint to get an order by ID
    @GET
    @Path("/{id}")
    public Response getOrderById(@PathParam("id") String id) {
        Ordine ordine = orderService.getOrderById(new ObjectId(id));
        if (ordine != null) {
            return Response.ok(ordine).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    // Endpoint to update order status
    @PUT
    @Path("/{id}/status")
    public Response updateOrderStatus(@PathParam("id") String id, String status) {
        Ordine updatedOrder = orderService.updateOrderStatus(new ObjectId(id), status);
        if (updatedOrder != null) {
            return Response.ok(updatedOrder).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    // Endpoint to cancel an order
    @DELETE
    @Path("/{id}")
    public Response cancelOrder(@PathParam("id") String id) {
        boolean success = orderService.cancelOrder(new ObjectId(id));
        if (success) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
