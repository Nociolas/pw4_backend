package it.itsincom.webdev2024.persistence.model;

import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

@MongoEntity(collection = "ordini")
public class Ordine {
    public ObjectId id;
    public int idUtente;  // Reference to MySQL id_utente
    public List<Prodotto> prodotti;
    public String stato;
    public double totale;
    public Date dataOrdine;
    public Date dataRitiro;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public int getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(int idUtente) {
        this.idUtente = idUtente;
    }

    public List<Prodotto> getProdotti() {
        return prodotti;
    }

    public void setProdotti(List<Prodotto> prodotti) {
        this.prodotti = prodotti;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public double getTotale() {
        return totale;
    }

    public void setTotale(double totale) {
        this.totale = totale;
    }

    public Date getDataOrdine() {
        return dataOrdine;
    }

    public void setDataOrdine(Date dataOrdine) {
        this.dataOrdine = dataOrdine;
    }

    public Date getDataRitiro() {
        return dataRitiro;
    }

    public void setDataRitiro(Date dataRitiro) {
        this.dataRitiro = dataRitiro;
    }
}

