package it.itsincom.webdev2024.rest;

import it.itsincom.webdev2024.persistence.model.Utente;
import it.itsincom.webdev2024.persistence.repository.UtenteRepository;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

public class UtenteResource {

    private final UtenteRepository utenteRepository;

    public UtenteResource(UtenteRepository utenteRepository) {
        this.utenteRepository = utenteRepository;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Utente registerUtente(Utente utente) {
        return utenteRepository.registerUtente(utente);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Utente> getAllUtenti() {
        return utenteRepository.getAllUtenti();
    }


}
