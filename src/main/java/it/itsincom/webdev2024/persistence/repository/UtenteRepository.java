package it.itsincom.webdev2024.persistence.repository;

import it.itsincom.webdev2024.persistence.model.Ruolo;
import it.itsincom.webdev2024.persistence.model.Utente;
import it.itsincom.webdev2024.rest.model.CreateProfileResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.Date;

@ApplicationScoped
public class UtenteRepository {

    private final DataSource dataSource;

    public UtenteRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public Utente registerUtente(Utente utente) {
        if (checkDouble(utente.getEmail())) {
            throw new BadRequestException("Un utente con questa email è già registrato");
        }
        try (Connection connection = dataSource.getConnection()) {
            Timestamp currentTimestamp = new Timestamp(new Date().getTime());
            utente.setDataRegistrazione(currentTimestamp);
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO utente " +
                            "(id_utente, nome_utente, email, telefono, password_hash, ruolo)" +
                            " VALUES (?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
                statement.setInt(1, utente.getId());
                statement.setString(2, utente.getNomeUtente());
                statement.setString(3, utente.getEmail());
                statement.setString(4, utente.getTelefono());
                statement.setString(5, utente.getPasswordHash());
                statement.setString(6, utente.getRuolo().name());
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

    public String generateVerificationCode() {
        return String.valueOf(new Random().nextInt(999999));
    }

    public void saveVerificationCode(int userId, String verificationCode) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "UPDATE utente SET codice_verifica = ? WHERE id_utente = ?")) {
                statement.setString(1, verificationCode);
                statement.setInt(2, userId);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean verifyCode(int userId, String code) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT COUNT(*) FROM utente WHERE id_utente = ? AND codice_verifica = ?")) {
                statement.setInt(1, userId);
                statement.setString(2, code);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    // Update the verification status
                    try (PreparedStatement updateStatement = connection.prepareStatement(
                            "UPDATE utente SET verificato = TRUE WHERE id_utente = ?")) {
                        updateStatement.setInt(1, userId);
                        updateStatement.executeUpdate();
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }


    public Optional<Utente> findUtenteByEmailPasswordHash(String email, String passwordHash, Boolean verificato) {
        try (Connection connection = dataSource.getConnection()) {
            String query = "SELECT id_utente, nome_utente, email, password_hash, ruolo, telefono, verificato " +
                    "FROM utente WHERE email = ? AND password_hash = ? AND verificato = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, email);
                statement.setString(2, passwordHash);
                statement.setBoolean(3, verificato);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    Utente utente = new Utente();
                    utente.setId(resultSet.getInt("id_utente"));
                    utente.setNomeUtente(resultSet.getString("nome_utente"));
                    utente.setEmail(resultSet.getString("email"));
                    utente.setPasswordHash(resultSet.getString("password_hash"));
                    utente.setRuolo(Ruolo.valueOf(resultSet.getString("ruolo")));
                    utente.setTelefono(resultSet.getString("telefono"));
                    utente.setVerificato(resultSet.getBoolean(1));

                    return Optional.of(utente);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public List<Utente> getAllUtenti() {
        List<Utente> listaUtenti = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT id_utente, nome_utente, email, telefono, password_hash, ruolo  FROM utente")) {
                var resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    var utente = new Utente();
                    utente.setId(resultSet.getInt("id_utente"));
                    utente.setNomeUtente(resultSet.getString("nome_utente"));
                    utente.setEmail(resultSet.getString("email"));
                    utente.setTelefono(resultSet.getString("telefono"));
                    utente.setPasswordHash(resultSet.getString("password_hash"));
                    utente.setRuolo(Ruolo.valueOf(resultSet.getString("ruolo")));
                    listaUtenti.add(utente);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return listaUtenti;
    }

    public CreateProfileResponse getUtenteById(int id) {
        CreateProfileResponse res = new CreateProfileResponse();
        try (Connection connection = dataSource.getConnection()) {
            String getUtenteByIdQuery = "SELECT id_utente, nome_utente, email, ruolo, telefono " +
                    "FROM utente WHERE id_utente = ?";
            try (PreparedStatement statement = connection.prepareStatement(getUtenteByIdQuery)) {
                statement.setInt(1, id);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    res.setId(resultSet.getInt("id_utente"));
                    res.setNomeUtente(resultSet.getString("nome_utente"));
                    res.setEmail(resultSet.getString("email"));
                    res.setRuolo(Ruolo.valueOf(resultSet.getString("ruolo")));
                    res.setTelefono(resultSet.getString("telefono"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    public CreateProfileResponse getUtenteByNome(String nome) {
        CreateProfileResponse res = new CreateProfileResponse();
        try (Connection connection = dataSource.getConnection()) {
            String getUtenteByNomeQuery = "SELECT id_utente, nome_utente, email, ruolo, telefono " +
                    "FROM utente WHERE nome_utente = ?";
            try (PreparedStatement statement = connection.prepareStatement(getUtenteByNomeQuery)) {
                statement.setString(1, nome);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    res.setId(resultSet.getInt("id_utente"));
                    res.setNomeUtente(resultSet.getString("nome_utente"));
                    res.setEmail(resultSet.getString("email"));
                    res.setRuolo(Ruolo.valueOf(resultSet.getString("ruolo")));
                    res.setTelefono(resultSet.getString("telefono"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return res;
    }



}
