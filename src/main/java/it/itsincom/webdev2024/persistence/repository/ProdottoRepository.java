package it.itsincom.webdev2024.persistence.repository;

import it.itsincom.webdev2024.persistence.model.Prodotto;
import jakarta.enterprise.context.ApplicationScoped;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@ApplicationScoped
public class ProdottoRepository {
    private final DataSource dataSource;

    public ProdottoRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Prodotto getProdotto(String nome) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM prodotto WHERE nome_prodotto = ?")) {
                statement.setString(1, nome);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    Prodotto prodotto = new Prodotto();
                    prodotto.setId(resultSet.getInt("id_prodotto"));
                    prodotto.setNome(resultSet.getString("nome_prodotto"));
                    prodotto.setDescrizione(resultSet.getString("descrizione"));
                    prodotto.setPrezzo(resultSet.getDouble("prezzo"));
                    prodotto.setIdCategoria(resultSet.getInt("id_categoria"));
                    prodotto.setImmagine(resultSet.getString("immagine"));
                    return prodotto;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public Prodotto addProdotto(Prodotto prodotto) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO prodotto " +
                            "(id_prodotto, nome_prodotto, descrizione, prezzo, id_categoria, immagine)" +
                            " VALUES (?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
                statement.setInt(1, prodotto.getId());
                statement.setString(2, prodotto.getNome());
                statement.setString(3, prodotto.getDescrizione());
                statement.setDouble(4, prodotto.getPrezzo());
                statement.setInt(5, prodotto.getIdCategoria());
                statement.setString(6, prodotto.getImmagine());
                statement.executeUpdate();
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    prodotto.setId(id);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return prodotto;
    }

    public Prodotto updateProdotto(Prodotto prodotto) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "UPDATE prodotto SET nome_prodotto = ?, descrizione = ?, prezzo = ?, id_categoria = ?, immagine = ? WHERE id_prodotto = ?")) {
                statement.setString(1, prodotto.getNome());
                statement.setString(2, prodotto.getDescrizione());
                statement.setDouble(3, prodotto.getPrezzo());
                statement.setInt(4, prodotto.getIdCategoria());
                statement.setString(5, prodotto.getImmagine());
                statement.setInt(6, prodotto.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return prodotto;
    }

    public void deleteProdotto(int id) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM prodotto WHERE id_prodotto = ?")) {
                statement.setInt(1, id);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}