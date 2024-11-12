package it.itsincom.webdev2024.rest;

import it.itsincom.webdev2024.persistence.model.Ordine;
import it.itsincom.webdev2024.rest.model.CreateOrderRequest;
import it.itsincom.webdev2024.service.OrdineService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;

@Path("/api/ordini")
public class OrdineResource {

    @Inject
    OrdineService ordineService;

    @POST
    public Response createOrder(CreateOrderRequest orderRequest) {
        try {
            Ordine createdOrder = ordineService.createOrderFromRequest(orderRequest);

            ordineService.sendOrderConfirmationEmail(createdOrder);

            return Response.status(Response.Status.CREATED).entity(createdOrder).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getOrderById(@PathParam("id") String id) {
        Ordine ordine = ordineService.getOrderById(new ObjectId(id));
        if (ordine != null) {
            return Response.ok(ordine).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @PUT
    @Path("/{id}/status")
    public Response updateOrderStatus(@PathParam("id") String id) {
        // Update the order status
        Ordine updatedOrder = ordineService.updateOrderStatus(new ObjectId(id));

        if (updatedOrder != null) {

            return Response.ok(updatedOrder).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response cancelOrder(@PathParam("id") String id) {
        ObjectId orderId = new ObjectId(id);
        Ordine ordine = ordineService.getOrderById(orderId);

        if (ordine != null) {
            boolean success = ordineService.cancelOrder(orderId);
            if (success) {

                return Response.noContent().build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Eliminazione fallita").build();
            }
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Ordine non trovato").build();
        }
    }


}
    }
