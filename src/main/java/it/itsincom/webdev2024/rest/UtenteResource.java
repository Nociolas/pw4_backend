package it.itsincom.webdev2024.rest;


import it.itsincom.webdev2024.persistence.model.Ruolo;
import it.itsincom.webdev2024.persistence.repository.SessionRepository;
import it.itsincom.webdev2024.persistence.repository.UtenteRepository;
import it.itsincom.webdev2024.rest.model.*;
import it.itsincom.webdev2024.service.AuthenticationService;
import it.itsincom.webdev2024.service.UtenteService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;


import java.sql.SQLException;
import java.util.List;

@Path("/api/utenti")
public class UtenteResource {

    private final UtenteService utenteService;
    private final UtenteRepository utenteRepository;
    private final AuthenticationService authenticationService;

    public UtenteResource(UtenteService utenteService, UtenteRepository utenteRepository, AuthenticationService authenticationService) {
        this.utenteService = utenteService;
        this.utenteRepository = utenteRepository;
        this.authenticationService = authenticationService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<CreateProfileResponse> getAllUtenti(@CookieParam("SESSION_COOKIE") @DefaultValue("-1") int sessionId) throws SQLException {
        CreateProfileResponse profile = authenticationService.getProfile(sessionId);
        if (profile == null || profile.getRuolo() != Ruolo.amministratore) {
            throw new RuntimeException("Non sei autorizzato a visualizzare tutti gli utenti");
        }
        return utenteService.getAllUtenti();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public CreateProfileResponse getUtenteById(@CookieParam("SESSION_COOKIE") @DefaultValue("-1") int sessionId, @PathParam("id") int id) {
        CreateProfileResponse profile = authenticationService.getProfile(sessionId);
        if (profile == null || profile.getRuolo() != Ruolo.amministratore) {
            throw new RuntimeException("Non sei autorizzato a visualizzare un utente");
        }
        return utenteRepository.getUtenteById(id);
    }

    @GET
    @Path("/find/{nome}")
    @Produces(MediaType.APPLICATION_JSON)
    public CreateProfileResponse getUtenteByNome(@CookieParam("SESSION_COOKIE") @DefaultValue("-1") int sessionId, @PathParam("nome") String nome) {
        CreateProfileResponse profile = authenticationService.getProfile(sessionId);
        if (profile == null || profile.getRuolo() != Ruolo.amministratore) {
            throw new RuntimeException("Non sei autorizzato a cercare un utente");
        }
        return utenteRepository.getUtenteByNome(nome);
    }

}