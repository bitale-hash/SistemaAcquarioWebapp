package it.acquario.dao;

import it.acquario.model.Utente;
import it.acquario.util.DBConnessione;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UtenteDAO {

    public Utente validaLogin(String username, String password) {
        Utente utente = null;
        // La query cerca una corrispondenza esatta per username e password
        String sql = "SELECT * FROM utenti WHERE username = ? AND password = ?";

        try (Connection conn = DBConnessione.getConnessione();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Se troviamo l'utente, popoliamo l'oggetto Modello
                    utente = new Utente();
                    utente.setId(rs.getInt("id_utente"));
                    utente.setUsername(rs.getString("username"));
                    utente.setEmail(rs.getString("email")); // Fondamentale per i tuoi allarmi!
                    utente.setRuolo(rs.getString("ruolo"));
                    // Non settiamo la password nell'oggetto per sicurezza
                }
            }
        } catch (SQLException e) {
            System.out.println("Errore durante il login nel DAO: " + e.getMessage());
            e.printStackTrace();
        }
        return utente;
    }
}