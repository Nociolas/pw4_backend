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

    @GET
    public Response getAllOrders() {
        return Response.ok(ordineService.getAllOrders()).build();
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

    @PUT
    @Path("/{id}/accept")
    public Response acceptOrder(@PathParam("id") String id) {
        Ordine updatedOrder = ordineService.acceptOrder(new ObjectId(id));
        if (updatedOrder != null) {
            ordineService.sendOrderAcceptedEmail(updatedOrder);
            return Response.ok(updatedOrder).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @PUT
    @Path("/{id}/cancel")
    public Response rejectOrdine(@PathParam("id") String id) {
        Ordine updatedOrder = ordineService.cancelOrder(new ObjectId(id));
        if (updatedOrder != null) {
            ordineService.sendOrderRejectedEmail(updatedOrder);
            return Response.ok(updatedOrder).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
