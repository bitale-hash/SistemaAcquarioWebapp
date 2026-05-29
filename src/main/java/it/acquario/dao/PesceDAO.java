package it.acquario.dao;

import it.acquario.model.Pesce;
import it.acquario.util.DBConnessione;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PesceDAO {

    //CRUD
    public List<Pesce> getAllPesci() throws SQLException {
        List<Pesce> lista = new ArrayList<>();
        String sql = "SELECT * FROM pesci";
        
        try (Connection conn = DBConnessione.getConnessione();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                lista.add(mapResultSetToPesce(rs));
            }
        }
        return lista;
    }
    
    public boolean insertPesce(Pesce p) throws SQLException {
        String sql = "INSERT INTO pesci (specie, tipo_cibo, temp_min, temp_max, ph_min, ph_max, num_max_mc, segnali_stress) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnessione.getConnessione();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, p.getSpecie());
            ps.setString(2, p.getTipoCibo());
            ps.setDouble(3, p.getTempMin());
            ps.setDouble(4, p.getTempMax());
            ps.setFloat(5, p.getPhMin());
            ps.setFloat(6, p.getPhMax());
            ps.setInt(7, p.getNumMaxMc());
            ps.setString(8, p.getSegnaliStress());
            
            return ps.executeUpdate() > 0;
             
        }
    }
    
    public boolean eliminaPesce(int idPesce) {
        String sql = "DELETE FROM pesci WHERE id_Pesce = ?"; // Assicurati che il nome tabella sia corretto
        
        try (Connection conn = DBConnessione.getConnessione(); // Usa il tuo metodo per la connessione
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idPesce);
            
            int righeColpite = ps.executeUpdate();
            return righeColpite > 0; // Ritorna true se ha effettivamente eliminato un record
            
        } catch (SQLException e) {
            System.err.println("Errore durante l'eliminazione del pesce: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
     
    public boolean updatePesce(Pesce p) {
        String sql = "UPDATE pesci SET specie=?, tipo_cibo=?, temp_min=?, temp_max=?, ph_min=?, ph_max=?, num_max_mc=?, segnali_stress=? WHERE id_pesce=?";
        try (Connection conn = DBConnessione.getConnessione();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getSpecie());
            ps.setString(2, p.getTipoCibo());
            ps.setDouble(3, p.getTempMin());
            ps.setDouble(4, p.getTempMax());
            ps.setFloat(5, p.getPhMin());
            ps.setFloat(6, p.getPhMax());
            ps.setInt(7, p.getNumMaxMc());
            ps.setString(8, p.getSegnaliStress());
            ps.setInt(9, p.getIdPesce());
            return ps.executeUpdate() > 0;
            
            
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
    // Recupera un singolo pesce per ID
    public Pesce getPesceById(int id) throws SQLException {
        String sql = "SELECT * FROM pesci WHERE id_pesce = ?";
        try (Connection conn = DBConnessione.getConnessione();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPesce(rs);
                }
            }
        }
        return null;
    }

    // Metodo di utility privato per mappare i risultati
    private Pesce mapResultSetToPesce(ResultSet rs) throws SQLException {
        Pesce p = new Pesce();
        p.setIdPesce(rs.getInt("id_pesce"));
        p.setSpecie(rs.getString("specie"));
        p.setTipoCibo(rs.getString("tipo_cibo"));
        p.setTempMin(rs.getDouble("temp_min"));
        p.setTempMax(rs.getDouble("temp_max"));
        p.setPhMin(rs.getFloat("ph_min"));  
        p.setPhMax(rs.getFloat("ph_max"));
        p.setNumMaxMc(rs.getInt("num_max_mc"));
        p.setSegnaliStress(rs.getString("segnali_stress"));
        return p;
    }
    
    
    
    public Pesce getSoglieBiologicheOttimali() {
        Pesce soglie = new Pesce();
        String query = "SELECT MAX(temp_min) AS t_min, MIN(temp_max) AS t_max, " +
                       "MAX(ph_min) AS p_min, MIN(ph_max) AS p_max FROM pesci";
        
        try (Connection conn = DBConnessione.getConnessione(); 
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next() && rs.getObject("t_min") != null) {
                soglie.setTempMin(rs.getDouble("t_min"));
                soglie.setTempMax(rs.getDouble("t_max"));
                soglie.setPhMin(rs.getFloat("p_min"));
                soglie.setPhMax(rs.getFloat("p_max"));
                return soglie;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Default fisso
        soglie.setTempMin(20.0);
        soglie.setTempMax(26.0);
        soglie.setPhMin(6.5f);
        soglie.setPhMax(8.0f);
        return soglie;
    }
}