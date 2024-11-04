package it.itsincom.webdev2024.persistence.model;

import java.sql.Timestamp;

public class Sessione {

    private int id;
    private Timestamp dataCreazione;
    private int utenteId;


    // GETTER E SETTER
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getDataCreazione() {
        return dataCreazione;
    }

    public void setDataCreazione(Timestamp dataCreazione) {
        this.dataCreazione = dataCreazione;
    }

    public int getUtente() {
        return utenteId;
    }

    public void setUtente(int utente) {
        this.utenteId = utente;
    }

}
