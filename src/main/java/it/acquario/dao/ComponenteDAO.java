 package it.acquario.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.acquario.model.Componente;
import it.acquario.util.DBConnessione;

public class ComponenteDAO {

    // Recupera tutti i componenti con i nuovi stati rotto e difettoso
    public List<Componente> getAllComponenti() {
        List<Componente> lista = new ArrayList<>();
        String query = "SELECT * FROM componenti_fisici";

        try (Connection conn = DBConnessione.getConnessione();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mappaComponente(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Metodo specifico per la DASHBOARD: recupera solo le criticità
    public List<Componente> getComponentiCritici() {
        List<Componente> lista = new ArrayList<>();
        // Logica: Rotto OPPURE Difettoso OPPURE Vecchio OPPURE Manutenzione scade tra meno di 30gg
        String query = "SELECT * FROM componenti_fisici " +
                "WHERE rotto = TRUE " +
                "OR difettoso = TRUE " +
                "OR DATE_ADD(data_acquisto, INTERVAL vita_media_mesi MONTH) < CURDATE() " +
                "OR DATEDIFF(DATE_ADD(ultima_manutenzione, INTERVAL manutenzione_ogni_giorni DAY), CURDATE()) < 30";
        
        try (Connection conn = DBConnessione.getConnessione();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mappaComponente(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public Componente getComponenteById(int id) {
        String sql = "SELECT * FROM componenti_fisici WHERE id_componente = ?";
        try (Connection conn = DBConnessione.getConnessione();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mappaComponente(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateComponente(Componente c) {
        String query = "UPDATE componenti_fisici SET nome=?, prezzo=?, data_acquisto=?, " +
                       "manutenzione_ogni_giorni=?, vita_media_mesi=?, note=?, ultima_manutenzione=?, " +
                       "rotto=?, difettoso=? WHERE id_componente=?";
        
        try (Connection conn = DBConnessione.getConnessione();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, c.getNome());
            ps.setBigDecimal(2, c.getPrezzo());
            ps.setDate(3, c.getData_acquisto());
            ps.setInt(4, c.getManutenzione_ogni_giorni());
            ps.setInt(5, c.getVita_media_mesi());
            ps.setString(6, c.getNote());
            ps.setDate(7, c.getUltima_manutenzione());
            ps.setBoolean(8, c.isRotto());      // Nuovo campo
            ps.setBoolean(9, c.isDifettoso());  // Nuovo campo
            ps.setInt(10, c.getId_componente());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean inserisciComponente(Componente c) {
        String query = "INSERT INTO componenti_fisici (nome, prezzo, data_acquisto, " +
                       "manutenzione_ogni_giorni, vita_media_mesi, note, ultima_manutenzione, rotto, difettoso) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnessione.getConnessione();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, c.getNome());
            ps.setBigDecimal(2, c.getPrezzo());
            ps.setDate(3, c.getData_acquisto());
            ps.setInt(4, c.getManutenzione_ogni_giorni());
            ps.setInt(5, c.getVita_media_mesi());
            ps.setString(6, c.getNote());
            ps.setDate(7, c.getUltima_manutenzione());
            ps.setBoolean(8, c.isRotto());
            ps.setBoolean(9, c.isDifettoso());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Metodo di utilità interno per non ripetere il mapping
    private Componente mappaComponente(ResultSet rs) throws SQLException {
        Componente c = new Componente();
        c.setId_componente(rs.getInt("id_componente"));
        c.setNome(rs.getString("nome"));
        c.setPrezzo(rs.getBigDecimal("prezzo"));
        c.setData_acquisto(rs.getDate("data_acquisto"));
        c.setManutenzione_ogni_giorni(rs.getInt("manutenzione_ogni_giorni"));
        c.setVita_media_mesi(rs.getInt("vita_media_mesi"));
        c.setFoto_scontrino_path(rs.getString("foto_scontrino_path"));
        c.setNote(rs.getString("note"));
        c.setUltima_manutenzione(rs.getDate("ultima_manutenzione"));
        c.setRotto(rs.getBoolean("rotto"));
        c.setDifettoso(rs.getBoolean("difettoso"));
        return c;
    }

    public boolean resetManutenzione(int id) {
        // Aggiorniamo 'ultima_manutenzione' alla data odierna
    	String query = "UPDATE componenti_fisici SET ultima_manutenzione = CURDATE(), rotto = FALSE WHERE id_componente = ?";
        try (Connection conn = DBConnessione.getConnessione();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, id);
            int righeColpite = ps.executeUpdate();
            
            return righeColpite > 0;
            
        } catch (SQLException e) {
            System.err.println("Errore resetManutenzione: " + e.getMessage());
            return false;
        }
    }
    
    public boolean eliminaComponente(int id) {
        String query = "DELETE FROM componenti_fisici WHERE id_componente = ?";
        try (Connection conn = DBConnessione.getConnessione();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, id);
            int righe = ps.executeUpdate();
            return righe > 0;
            
        } catch (SQLException e) {
            System.err.println("Errore eliminazione: " + e.getMessage());
            return false;
        }
    }
    
    public int countRotti() {
        int count = 0;
        String query = "SELECT COUNT(*) FROM componenti_fisici WHERE rotto = 1"; // tinyint(1)
        try (Connection conn = DBConnessione.getConnessione(); 
            PreparedStatement ps = conn.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) 
            	count = rs.getInt(1);
        } catch (SQLException e) {
        	e.printStackTrace(); 
        }
        return count;
    }
    
    public int countDifettosi() {
        int count = 0;
        String query = "SELECT COUNT(*) FROM componenti_fisici WHERE difettoso = 1"; // tinyint(1)
        try (Connection conn = DBConnessione.getConnessione(); 
            PreparedStatement ps = conn.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) 
            	count = rs.getInt(1);
        } catch (SQLException e) {
        	e.printStackTrace(); 
        }
        return count;
    }
 }   
  