package it.itsincom.webdev2024.rest;

import it.itsincom.webdev2024.persistence.model.Prodotto;
import it.itsincom.webdev2024.service.AuthenticationService;
import it.itsincom.webdev2024.service.ProdottoService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/prodotti")
public class ProdottoResource {

    private final ProdottoService prodottoService;

    public ProdottoResource(ProdottoService prodottoService) {
        this.prodottoService = prodottoService;
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
        prodottoService.addProdotto(prodotto);
        return Response.ok().build();
    }

    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateProdotto(Prodotto prodotto) {
        prodottoService.updateProdotto(prodotto);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteProdotto(@PathParam("id") int id) {
        prodottoService.deleteProdotto(id);
        return Response.ok().build();
    }

}
