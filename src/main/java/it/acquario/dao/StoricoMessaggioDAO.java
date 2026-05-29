package it.acquario.dao;

import it.acquario.model.StoricoMessaggio;
import it.acquario.util.DBConnessione;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StoricoMessaggioDAO {

    // --- METODO 1: Recupera gli ultimi N messaggi per la Dashboard ---
    public List<StoricoMessaggio> getUltimiMessaggi(int limit) {
        List<StoricoMessaggio> lista = new ArrayList<>();
        String query = "SELECT id_history, tipo_controllo, esito, origine, messaggio, elaborato, timestamp " +
                "FROM storico_messaggi " +
                "ORDER BY timestamp DESC LIMIT ?";
        
        try (Connection conn = DBConnessione.getConnessione();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, limit);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StoricoMessaggio msg = new StoricoMessaggio();
                    msg.setIdHistory(rs.getInt("id_history"));
                    msg.setTipoControllo(rs.getString("tipo_controllo"));
                    msg.setEsito(rs.getString("esito"));
                    msg.setOrigine(rs.getString("origine"));
                    msg.setMessaggio(rs.getString("messaggio"));
                    msg.setElaborato(rs.getBoolean("elaborato"));
                    msg.setTimestamp(rs.getTimestamp("timestamp"));
                    
                    lista.add(msg);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // --- METODO 2: Inserisce un nuovo messaggio/allarme ---
    public boolean inserisciMessaggio(StoricoMessaggio msg) {
    	String query = "INSERT INTO storico_messaggi (tipo_controllo, esito, origine, messaggio, elaborato, timestamp) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnessione.getConnessione();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, msg.getTipoControllo());
            ps.setString(2, msg.getEsito());
            ps.setString(3, msg.getOrigine() != null ? msg.getOrigine() : "SISTEMA"); // Default se ti dimentichi di settarlo
            ps.setString(4, msg.getMessaggio());
            ps.setBoolean(5, msg.isElaborato());
            // Se nel model il timestamp è nullo, usiamo l'ora corrente del sistema
            ps.setTimestamp(6, msg.getTimestamp() != null ? msg.getTimestamp() : new java.sql.Timestamp(System.currentTimeMillis()));
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- METODO 3: Marca un messaggio come elaborato/letto ---
    public boolean marcaComeElaborato(int idHistory) {
        String query = "UPDATE storico_messaggi SET elaborato = 1 WHERE id_history = ?";
        
        try (Connection conn = DBConnessione.getConnessione();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, idHistory);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
 // --- METODO 4: Recupera tutti i messaggi NON ancora elaborati ---
    public List<StoricoMessaggio> getMessaggiDaElaborare() {
        List<StoricoMessaggio> lista = new ArrayList<>();
        String query = "SELECT id_history, tipo_controllo, esito, origine, messaggio, elaborato, timestamp " +
                       "FROM storico_messaggi WHERE elaborato = 0 ORDER BY timestamp ASC"; // ASC per elaborarli dal più vecchio al più recente
        
        try (Connection conn = DBConnessione.getConnessione();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                StoricoMessaggio msg = new StoricoMessaggio();
                msg.setIdHistory(rs.getInt("id_history"));
                msg.setTipoControllo(rs.getString("tipo_controllo"));
                msg.setEsito(rs.getString("esito"));
                msg.setOrigine(rs.getString("origine"));
                msg.setMessaggio(rs.getString("messaggio"));
                msg.setElaborato(rs.getBoolean("elaborato"));
                msg.setTimestamp(rs.getTimestamp("timestamp"));
                
                lista.add(msg);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}