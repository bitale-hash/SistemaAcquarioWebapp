package it.acquario.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.acquario.model.Categoria;
import it.acquario.model.Configurazione;
import it.acquario.model.TipoDato;
import it.acquario.util.DBConnessione; // Usiamo la tua classe!

public class ConfigurazioneDAO {
 
    public List<Configurazione> getAllConfigurazioni() throws SQLException {
        List<Configurazione> lista = new ArrayList<>();
        String query = "SELECT id_config, parametro, valore, tipo_dato, categoria , descrizione, ultima_modifica, minimo, massimo FROM configurazione";

        // Usiamo il try-with-resources per chiudere automaticamente connessione e statement
        try (Connection conn = DBConnessione.getConnessione();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Configurazione c = new Configurazione();
                c.setIdConfig(rs.getInt("id_config"));
                c.setParametro(rs.getString("parametro"));
                c.setValore(rs.getString("valore"));
                
                // Conversione da Stringa ENUM del DB a Enum Java
                // Usiamo toUpperCase() per sicurezza (es. 'int' nel db diventa 'INT' per l'enum)
                String tipoStr = rs.getString("tipo_dato").toUpperCase();
                c.setTipoDato(TipoDato.valueOf(tipoStr)); 
                String catStr = rs.getString("categoria").toUpperCase();
                c.setCategoria(Categoria.valueOf(catStr));
                c.setDescrizione(rs.getString("descrizione"));
                c.setUltimaModifica(rs.getTimestamp("ultima_modifica"));
                c.setMinimo(rs.getDouble("minimo"));
                c.setMassimo(rs.getDouble("massimo"));
                lista.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Errore in getAllConfigurazioni: " + e.getMessage());
            throw e; // Rilanciamo l'eccezione per gestirla nella Servlet
        }
        return lista;
    }
 
    public Configurazione getConfigByParametro(String nome) throws SQLException {
        String sql = "SELECT * FROM configurazione WHERE parametro = ?";
        Configurazione conf = null;

        // Connessione ottenuta tramite il tuo metodo di utility (es. DBManager.getConnection())
        try (Connection conn = DBConnessione.getConnessione(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, nome);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    conf = new Configurazione();
                    
                    // Mappatura dei campi classici
                    conf.setIdConfig(rs.getInt("id_config"));
                    conf.setParametro(rs.getString("parametro"));
                    conf.setValore(rs.getString("valore"));
                    
                    // Gestione degli Enum (Conversione da String a Enum Java)
                    conf.setTipoDato(TipoDato.valueOf(rs.getString("tipo_dato").toUpperCase()));
                    conf.setCategoria(Categoria.valueOf(rs.getString("categoria").toUpperCase()));
                    
                    conf.setDescrizione(rs.getString("descrizione"));
                    conf.setUltimaModifica(rs.getTimestamp("ultima_modifica"));

                    // Mappatura dei nuovi campi per la validazione
                    conf.setMinimo(rs.getFloat("minimo"));
                    conf.setMassimo(rs.getFloat("massimo"));
                }
            }
        }
        return conf;
    }
    
    public void inserisciConfigurazione(Configurazione c) throws SQLException {
        String query = "INSERT INTO configurazione (parametro, descrizione, valore, categoria, tipo_dato) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnessione.getConnessione();  
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, c.getParametro());
            ps.setString(2, c.getDescrizione());
            ps.setString(3, c.getValore());
            // Trasformiamo l'enum in stringa per salvarlo sul DB
            ps.setString(4, c.getCategoria().name());
            ps.setString(5, c.getTipoDato().name());
            
            ps.executeUpdate();
        }
    }
 
    public void eliminaConfigurazione(String parametro) throws SQLException {
        String query = "DELETE FROM configurazione WHERE parametro = ?";
        
        try (Connection conn = DBConnessione.getConnessione(); 
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, parametro);
            ps.executeUpdate();
        }
    }
    
    public boolean updateConfigurazioneCompleta(Configurazione c) throws SQLException {
        String query = "UPDATE configurazione SET descrizione = ?, valore = ?, categoria = ?, tipo_dato = ? WHERE parametro = ?";
        
        try (Connection conn = DBConnessione.getConnessione();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, c.getDescrizione());
            ps.setString(2, c.getValore());
            ps.setString(3, c.getCategoria().name());
            ps.setString(4, c.getTipoDato().name());
            ps.setString(5, c.getParametro()); // Il WHERE si basa sul codice parametro
            
            return ps.executeUpdate() > 0;
        }
    }
    
    //non penso di usarlo
    /*public boolean updateConfigurazione(String parametro, String nuovoValore) throws SQLException {
        String query = "UPDATE configurazione SET valore = ? WHERE parametro = ?";
        
        try (Connection conn = DBConnessione.getConnessione();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, nuovoValore);
            ps.setString(2, parametro);
            
            return ps.executeUpdate() > 0;
        }
    }*/
}