package it.itsincom.webdev2024.rest.model;
import java.sql.Date;

public class CreateUtenteRequest {

    private int id;
    private String nomeUtente;
    private String email;
    private String telefono;
    private String password;
    private String ruolo;
//    private Date registrazione;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRuolo() {
        return ruolo;
    }

    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
    }

//    public Date getRegistrazione() {
//        return registrazione;
//    }
//
//    public void setRegistrazione(Date registrazione) {
//        this.registrazione = registrazione;
//    }
}
