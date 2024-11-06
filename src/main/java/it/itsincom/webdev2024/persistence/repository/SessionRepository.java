package it.itsincom.webdev2024.persistence.repository;

import it.itsincom.webdev2024.persistence.model.Sessione;
import jakarta.enterprise.context.ApplicationScoped;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@ApplicationScoped
public class SessionRepository {

    private final DataSource dataSource;

    public SessionRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    // CREA UNA NUOVO SESSIONE
    public int insertSession(int idUtente) throws SQLException {

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement
                    ("INSERT INTO sessioni (id_utente) VALUES (?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
                statement.setInt(1, idUtente);
                statement.executeUpdate();
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);

                    return id;
                }
            }
        }
        throw new SQLException("Cannot insert new session for user" + idUtente);
    }

    // ELIMINA UNA SESSIONE
    public void delete(int sessionId) {
        try {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement("DELETE FROM sessioni WHERE id_sessione = ?")) {
                    statement.setInt(1, sessionId);
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // OTTINE LA SESSIONE TRAMITE L'ID
    public Sessione getSessionById(int sessionId) {

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT id_sessione, id_utente, data_creazione FROM sessioni WHERE id_sessione = ?")) {
                statement.setInt(1, sessionId);
                ResultSet rs = statement.executeQuery();
                while (rs.next()) {
                    var sessione = new Sessione();
                    sessione.setId(rs.getInt("id_sessione"));
                    sessione.setUtenteId(rs.getInt("id_utente"));
                    sessione.setDataCreazione(rs.getTimestamp("data_creazione"));
                    return sessione;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        throw new RuntimeException("non è presente alcuna sessione con id" + sessionId);
    }

    // OTTIENE L'ID DELL'UTENTE TRAMITE L'ID DELLA SESSIONE
    public int getIdBySession(int sessionCookie) throws SQLException {
        ResultSet rs;
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT id_utente FROM sessioni WHERE id_sessione = ?")) {
                statement.setInt(1, sessionCookie);
                rs = statement.executeQuery();
                while (rs.next()) {
                    return rs.getInt("id_utente");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        throw new IllegalStateException("Nessun partecipante trovato per il cookie di sessione: " + sessionCookie);
    }
}
