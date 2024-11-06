package it.itsincom.webdev2024.service;

import it.itsincom.webdev2024.persistence.model.*;
import it.itsincom.webdev2024.persistence.repository.UtenteRepository;
import it.itsincom.webdev2024.rest.model.*;
import jakarta.enterprise.context.ApplicationScoped;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class UtenteService {

    private final HashCalculator hashCalculator;
    private final UtenteRepository utenteRepository;


    public UtenteService(UtenteRepository utenteRepository, HashCalculator hashCalculator) {
        this.utenteRepository = utenteRepository;
        this.hashCalculator = hashCalculator;

    }

    public CreateUtenteResponse createUtente(CreateUtenteRequest utente) {
        // 1. Estrarre la password dalla richiesta
        String password = utente.getPassword();
        // 2. Calcolare l'hash della password
        String hash = hashCalculator.calculateHash(password);
        // 3. Creare l'oggetto utente
        Utente u = new Utente();
        u.setNomeUtente(utente.getNomeUtente());
        u.setEmail(utente.getEmail());
        u.setTelefono(utente.getTelefono());
        u.setPasswordHash(hash);
        u.setDataRegistrazione(new Timestamp(System.currentTimeMillis()));
        u.setRuolo(Ruolo.valueOf(utente.getRuolo()));
        u.setVerificato(false);

        // 4. Salvare l'oggetto utente nel database
        Utente creato = utenteRepository.registerUtente(u);

        // 5. Convertire l'oggetto utente in CreateUtenteResponse
        CreateUtenteResponse response = new CreateUtenteResponse();
        response.setId(creato.getId());
        response.setNomeUtente(creato.getNomeUtente());
        response.setEmail(creato.getEmail());
        response.setTelefono(creato.getTelefono());
        response.setRuolo(creato.getRuolo());
        response.setVerificato(creato.getVerificato());

        // 6. Restituire CreateUtenteResponse
        return response;
    }

    // OTTIENE LA LISTA DEGLI UTENTI CON RELATIVO INDIRIZZO
    public List<CreateProfileResponse> getAllUtenti() throws SQLException {
        List<CreateProfileResponse> responses = new ArrayList<>();
        List<Utente> utenti = utenteRepository.getAllUtenti();
        for (Utente utente : utenti) {
            CreateProfileResponse res = convertToProfileResponse(utente);
            responses.add(res);
        }
        return responses;
    }

    private CreateProfileResponse convertToProfileResponse(Utente u) {
        CreateProfileResponse res = new CreateProfileResponse();
        res.setId(u.getId());
        res.setNomeUtente(u.getNomeUtente());
        res.setEmail(u.getEmail());
        res.setTelefono(u.getTelefono());
        res.setRuolo(u.getRuolo());
        res.setVerificato(u.getVerificato());
        return res;
    }

}