package it.itsincom.webdev2024.rest;

import it.itsincom.webdev2024.persistence.model.Prodotto;
import it.itsincom.webdev2024.service.AuthenticationService;
import it.itsincom.webdev2024.service.ProdottoService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/prodotti")
public class ProdottoResource {

    private final ProdottoService prodottoService;

    public ProdottoResource(ProdottoService prodottoService) {
        this.prodottoService = prodottoService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Prodotto> getAllProdotti() {
        return prodottoService.getAllProdotti();
    }

    @GET
    @Path("/find/{nome}")
    @Produces(MediaType.APPLICATION_JSON)
    public Prodotto getProdotto(@PathParam("nome") String nome) {
        return prodottoService.getProdotto(nome);
    }

    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addProdotto(Prodotto prodotto) {
        Prodotto addedProdotto = prodottoService.addProdotto(prodotto);
        return Response.status(Response.Status.CREATED)
                .entity(addedProdotto)
                .build();
    }

    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateProdotto(Prodotto prodotto) {
        Prodotto updatedProdotto = prodottoService.updateProdotto(prodotto);
        return Response.ok(updatedProdotto).build(); // 200 OK with the updated entity
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteProdotto(@PathParam("id") int id) {
        prodottoService.deleteProdotto(id);
        return Response.ok("Prodotto deleted successfully").build(); // 200 OK with a message
    }


}
