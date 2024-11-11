package it.itsincom.webdev2024.persistence.model;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.types.ObjectId;

@MongoEntity(collection = "notifiche")
public class Notifica extends PanacheMongoEntity {
    public ObjectId id;
    public int id_utente;  // Reference to MySQL id_utente
    public String messaggio;
    public String tipo;
    public String stato;
    public String data_invio;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public int getId_utente() {
        return id_utente;
    }

    public void setId_utente(int id_utente) {
        this.id_utente = id_utente;
    }

    public String getMessaggio() {
        return messaggio;
    }

    public void setMessaggio(String messaggio) {
        this.messaggio = messaggio;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public String getData_invio() {
        return data_invio;
    }

    public void setData_invio(String data_invio) {
        this.data_invio = data_invio;
    }
}

