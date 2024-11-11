package it.itsincom.webdev2024.rest.model;

public class ProductOrderRequest {
    private String nome;
    private Integer quantita;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getQuantita() {
        return quantita;
    }

    public void setQuantita(Integer quantita) {
        this.quantita = quantita;
    }
}
