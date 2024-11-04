package it.itsincom.webdev2024.persistence.repository;

import it.itsincom.webdev2024.persistence.model.Utente;
import it.itsincom.webdev2024.service.HashCalculator;
import jakarta.ws.rs.BadRequestException;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UtenteRepository {

    private final DataSource dataSource;
    private final HashCalculator hashCalculator;

    public UtenteRepository(DataSource dataSource, HashCalculator hashCalculator) {
        this.dataSource = dataSource;
        this.hashCalculator = hashCalculator;
    }

    public Utente registerUtente(Utente utente) {
        if (checkDouble(utente.getEmail())) {
            throw new BadRequestException("Un utente con questa email è già registrato");
        }
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO utente (id_utente, nome_utente, email, password_hash) VALUES (?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
                statement.setInt(1, utente.getId());
                statement.setString(4, utente.getNomeUtente());
                statement.setString(4, utente.getEmail());
                statement.setString(5, utente.getPasswordHash());
                statement.executeUpdate();
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    utente.setId(id);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return utente;
    }

    // OTTIENE LA LISTA DEGLI UTENTI
    public List<Utente> getAllUtenti() {
        List<Utente> listaUtenti = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT id_utente, nome_utente, email, password_hash FROM utente")) {
                var resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    var utente = new Utente();
                    utente.setId(resultSet.getInt("id_utente"));
                    utente.setEmail(resultSet.getString("email"));
                    utente.setPasswordHash(resultSet.getString("password_hash"));
                    utente.setTelefono(resultSet.getString("telefono"));
                    listaUtenti.add(utente);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return listaUtenti;
    }

    // CONTROLLA SE E' GIA' PRESENTE UN UTENTE CON LA STESSA EMAIL

    public boolean checkDouble(String email) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM utente WHERE email = ?")) {
                statement.setString(1, email);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }



}
