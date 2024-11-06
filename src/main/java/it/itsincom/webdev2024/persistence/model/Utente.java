package it.itsincom.webdev2024.persistence.model;

import java.util.Date;

public class Utente {
    private int id;
    private String nomeUtente;
    private String email;
    private String telefono;
    private String passwordHash;
    private Date dataRegistrazione;
    private Ruolo ruolo;
    private Boolean verificato;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomeUtente() {
        return nomeUtente;
    }

    public void setNomeUtente(String nomeUtente) {
        this.nomeUtente = nomeUtente;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Date getDataRegistrazione() {
        return dataRegistrazione;
    }

    public void setDataRegistrazione(Date dataRegistrazione) {
        this.dataRegistrazione = dataRegistrazione;
    }

    public Ruolo getRuolo() {
        return ruolo;
    }

    public void setRuolo(Ruolo ruolo) {
        this.ruolo = ruolo;
    }

    public Boolean getVerificato() {
        return verificato;
    }

    public void setVerificato(Boolean verificato) {
        this.verificato = verificato;
    }
}
