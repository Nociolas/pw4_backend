package it.itsincom.webdev2024.persistence.repository;

import it.itsincom.webdev2024.persistence.model.Ordine;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.types.ObjectId;
import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;

import java.util.List;

@ApplicationScoped
public class OrdineRepository implements PanacheMongoRepositoryBase<Ordine, ObjectId> {

    public List<Ordine> getAllOrdini() {
        return listAll();
    }

    public Ordine saveOrder(Ordine ordine) {
        this.persist(ordine);
        return ordine;
    }

    public Ordine findOrderById(ObjectId id) {
        return findById(id);
    }

    public List<Ordine> findOrdersByUserId(int userId) {
        return list("id_utente", userId);
    }

    // Update order status
    public Ordine updateOrderStatus(ObjectId orderId) {
        Ordine ordine = findOrderById(orderId);
        if (ordine != null) {
            ordine.stato = "accettato";
            persistOrUpdate(ordine);
        }
        return ordine;
    }

    // Delete an order (if required)
    public boolean deleteOrder(ObjectId orderId) {
        Ordine ordine = findOrderById(orderId);
        if (ordine != null) {
            delete(ordine);
            return true;
        }
        return false;
    }


}
