package it.itsincom.webdev2024.persistence.model;

public class Admin {
    private int id;
    private String nomeAdmin;
    private String emailAdmin;
    private String passwordHashAdmin;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomeAdmin() {
        return nomeAdmin;
    }

    public void setNomeAdmin(String nomeAdmin) {
        this.nomeAdmin = nomeAdmin;
    }

    public String getEmailAdmin() {
        return emailAdmin;
    }

    public void setEmailAdmin(String emailAdmin) {
        this.emailAdmin = emailAdmin;
    }

    public String getPasswordHashAdmin() {
        return passwordHashAdmin;
    }

    public void setPasswordHashAdmin(String passwordHashAdmin) {
        this.passwordHashAdmin = passwordHashAdmin;
    }
}
