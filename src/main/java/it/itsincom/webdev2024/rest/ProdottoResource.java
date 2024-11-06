package it.itsincom.webdev2024.rest;

import it.itsincom.webdev2024.persistence.model.Prodotto;
import it.itsincom.webdev2024.persistence.model.Ruolo;
import it.itsincom.webdev2024.persistence.repository.ProdottoRepository;
import it.itsincom.webdev2024.rest.model.CreateProfileResponse;
import it.itsincom.webdev2024.service.AuthenticationService;
import it.itsincom.webdev2024.service.ProdottoService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
@Path("/prodotti")
public class ProdottoResource {
    private final ProdottoService prodottoService;
    private final ProdottoRepository prodottoRepository;


    public ProdottoResource(ProdottoService prodottoService, ProdottoRepository prodottoRepository, AuthenticationService authenticationService) {
        this.prodottoService = prodottoService;
        this.prodottoRepository = prodottoRepository;
    }
    @GET
    @Path("/find/{nome}")
    @Produces(MediaType.APPLICATION_JSON)
    public Prodotto getProdotto(@PathParam("nome") String nome) {
        return prodottoRepository.getProdotto(nome);
    }
    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addProdotto(Prodotto prodotto){
       prodottoRepository.addProdotto(prodotto);
       return Response.ok().build();
    }


    @DELETE
    @Path("/{id}")
    public Response deleteProdotto (@PathParam("id") int id){
        prodottoRepository.deleteProdotto(id);
        return Response.ok().build();
    }

}
