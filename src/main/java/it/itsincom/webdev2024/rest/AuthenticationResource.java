package it.itsincom.webdev2024.rest;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import it.itsincom.webdev2024.persistence.repository.UtenteRepository;
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

import java.util.Random;

@Path("/api/auth")
public class AuthenticationResource {

    @Inject
    Mailer mailer;

    private final AuthenticationService authenticationService;
    private final UtenteService utenteService;
    private final UtenteRepository utenteRepository;

    public AuthenticationResource(AuthenticationService authenticationService, UtenteService utenteService, UtenteRepository utenteRepository) {
        this.authenticationService = authenticationService;
        this.utenteService = utenteService;
        this.utenteRepository = utenteRepository;
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public CreateUtenteResponse register(CreateUtenteRequest request) {
        CreateUtenteResponse response = utenteService.createUtente(request);
        String verificationCode = utenteRepository.generateVerificationCode();
        utenteRepository.saveVerificationCode(response.getId(), verificationCode);
        mailer.send(Mail.withText("mossalimattia@gmail.com", "Verification Code", "Your verification code is: " + verificationCode));
        return response;
    }

    @POST
    @Path("/verify")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response verifyCode(JsonObject verifyRequest) {
        int userId = verifyRequest.getInt("id");
        String code = verifyRequest.getString("codiceVerifica");

        boolean isVerified = utenteService.verifyCode(userId, code);
        if (isVerified) {
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid verification code").build();
        }
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