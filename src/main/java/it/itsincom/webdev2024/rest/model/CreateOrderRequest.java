package it.itsincom.webdev2024.rest.model;

import java.util.List;

public class CreateOrderRequest {
    private int idUtente;
    private List<ProductOrderRequest> prodotti;

    public int getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(int idUtente) {
        this.idUtente = idUtente;
    }

    public List<ProductOrderRequest> getProdotti() {
        return prodotti;
    }

    public void setProdotti(List<ProductOrderRequest> prodotti) {
        this.prodotti = prodotti;
    }
}

