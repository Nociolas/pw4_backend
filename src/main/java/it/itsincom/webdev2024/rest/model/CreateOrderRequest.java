package it.itsincom.webdev2024.rest.model;

import java.util.Date;
import java.util.List;

public class CreateOrderRequest {
    private int idUtente;
    private List<ProductOrderRequest> prodotti;
    private Date dataRitiro;
    private String commento;

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

    public Date getDataRitiro() {
        return dataRitiro;
    }

    public void setDataRitiro(Date dataRitiro) {
        this.dataRitiro = dataRitiro;
    }

    public String getCommento() {
        return commento;
    }

    public void setCommento(String commento) {
        this.commento = commento;
    }
}

