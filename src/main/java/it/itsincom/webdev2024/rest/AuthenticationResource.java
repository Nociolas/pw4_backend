package it.itsincom.webdev2024.rest;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import it.itsincom.webdev2024.rest.model.CreateProfileResponse;
import it.itsincom.webdev2024.rest.model.CreateUtenteRequest;
import it.itsincom.webdev2024.rest.model.CreateUtenteResponse;
import it.itsincom.webdev2024.service.AuthenticationService;
import it.itsincom.webdev2024.service.UtenteService;
import it.itsincom.webdev2024.service.exception.SessionCreationException;
import it.itsincom.webdev2024.service.exception.WrongUsernameOrPasswordException;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;

@Path("/api/auth")
public class AuthenticationResource {

    @Inject
    Mailer mailer;

    private final AuthenticationService authenticationService;
    private final UtenteService utenteService;

    public AuthenticationResource(AuthenticationService authenticationService, UtenteService utenteService) {
        this.authenticationService = authenticationService;
        this.utenteService = utenteService;
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public CreateUtenteResponse register(CreateUtenteRequest request) {
        mailer.send(Mail.withText("mossalimattia@gmail.com", "A simple gay from quarkus", "This is your body and i like it"));;
        return utenteService.createUtente(request);
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(JsonObject loginRequest) throws WrongUsernameOrPasswordException, SessionCreationException {
        String email = loginRequest.getString("email");
        String password = loginRequest.getString("password");

        int sessione = authenticationService.login(email, password);
        NewCookie sessionCookie = new NewCookie.Builder("SESSION_COOKIE").path("/").value(String.valueOf(sessione)).build();
        return Response.ok()
                .cookie(sessionCookie)
                .build();
    }

    @DELETE
    @Path("/logout")
    public Response logout(@CookieParam("SESSION_COOKIE") int sessionId) {
        authenticationService.logout(sessionId);
        NewCookie sessionCookie = new NewCookie.Builder("SESSION_COOKIE").path("/").build();
        return Response.ok()
                .cookie(sessionCookie)
                .build();
    }

    @GET
    @Path("/profile")
    @Produces(MediaType.APPLICATION_JSON)
    public CreateProfileResponse getProfile(@CookieParam("SESSION_COOKIE") @DefaultValue("-1") int sessionId) throws WrongUsernameOrPasswordException {
        if (sessionId == -1) {
            throw new WrongUsernameOrPasswordException();
        }
        return authenticationService.getProfile(sessionId);
    }
}