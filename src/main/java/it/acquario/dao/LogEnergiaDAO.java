package it.acquario.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import it.acquario.model.LogEnergia;
import it.acquario.util.DBConnessione; // Assicurati che il package sia corretto

public class LogEnergiaDAO {

    
    public LogEnergia getUltimoStato() throws SQLException {
        LogEnergia log = null;
        String query = "SELECT * FROM log_energia ORDER BY data_ora DESC LIMIT 1";

        try (Connection conn = DBConnessione.getConnessione();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                log = mapResultSetToLog(rs);
            }
        }
        return log;
    }
    // Metodo di utility per mappare i dati
    private LogEnergia mapResultSetToLog(ResultSet rs) throws SQLException {
        LogEnergia l = new LogEnergia();
        l.setIdLog(rs.getInt("id_log"));
        l.setDataOra(rs.getTimestamp("data_ora"));
        l.setLivelloBatteria(rs.getInt("livello_batteria"));
        l.setTensioneVolt(rs.getFloat("tensione_volt"));
        l.setCorrenteProdottaMa(rs.getFloat("corrente_prodotta_ma"));
        l.setCorrenteConsumataMa(rs.getFloat("corrente_consumata_ma"));
        l.setErroreRilevato(rs.getString("errore_rilevato"));
        return l;
    }
    
    public List<LogEnergia> getStoricoCompleto() throws SQLException {
        List<LogEnergia> lista = new ArrayList<>();
        String query = "SELECT * FROM log_energia ORDER BY data_ora DESC LIMIT 50";

        try (Connection conn = DBConnessione.getConnessione();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapResultSetToLog(rs));
            }
        }
        return lista;
    }
    
    public int getUltimoLivelloBatteria() {
        int livello = 100; // Valore di default
        // Ordiniamo per data/ora decrescente e prendiamo il primo record
        String query = "SELECT livello_batteria FROM log_energia ORDER BY data_ora DESC LIMIT 1";
        try (Connection conn = DBConnessione.getConnessione(); 
             PreparedStatement ps = conn.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                livello = rs.getInt("livello_batteria");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return livello;
    }
    
    public List<LogEnergia> getListaLuceGrafico() {
        List<LogEnergia> lista = new ArrayList<>();
        String query = "SELECT data_ora, corrente_prodotta_ma FROM (" +
                       "  SELECT data_ora, corrente_prodotta_ma FROM log_energia " +
                       "  ORDER BY data_ora DESC LIMIT 7" +
                       ") AS sub ORDER BY data_ora ASC";
        try (Connection conn = DBConnessione.getConnessione(); 
                PreparedStatement ps = conn.prepareStatement(query)) {
               ResultSet rs = ps.executeQuery();
               
               while (rs.next()) {
                   LogEnergia energia = new LogEnergia();
                   
                   // Mappiamo il timestamp
                   energia.setDataOra(rs.getTimestamp("data_ora"));
                   
                   // Mappiamo la corrente prodotta (usando il tipo float/double coerente con il tuo model)
                   energia.setCorrenteProdottaMa(rs.getFloat("corrente_prodotta_ma")); 
                   
                   // Aggiungiamo l'oggetto alla lista
                   lista.add(energia);
               }
               
           } catch (SQLException e) {
               e.printStackTrace();
           }
        return lista;
    }
    //METODI per l' AnalizzatoreMessaggi
    public List<LogEnergia> getUltimiStatiEnergetici(int limit) throws SQLException {
    List<LogEnergia> lista = new ArrayList<>();
    // Prende gli ultimi N record completi dal DB, ordinati dal più vecchio al più recente per l'analisi del trend
    String query = "SELECT livello_batteria, corrente_prodotta_ma, corrente_consumata_ma " +
                   "FROM ( " +
                   "  SELECT data_ora, livello_batteria, corrente_prodotta_ma, corrente_consumata_ma " +
                   "  FROM log_energia " +
                   "  ORDER BY data_ora DESC LIMIT ? " +
                   ") AS sub ORDER BY data_ora ASC";

    try (Connection conn = DBConnessione.getConnessione();
         PreparedStatement ps = conn.prepareStatement(query)) {
        
        ps.setInt(1, limit);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                LogEnergia le = new LogEnergia();
                le.setLivelloBatteria(rs.getInt("livello_batteria"));
                le.setCorrenteProdottaMa(rs.getFloat("corrente_prodotta_ma"));
                le.setCorrenteConsumataMa(rs.getFloat("corrente_consumata_ma"));
                lista.add(le);
            }
        }
    }
    return lista;
    }
    //CRUD
    public void inserisciLogEnergia(LogEnergia log) throws SQLException {
        String query = "INSERT INTO log_energia (livello_batteria, tensione_volt, " +
                       "corrente_prodotta_ma, corrente_consumata_ma, errore_rilevato) " +
                       "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnessione.getConnessione();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, log.getLivelloBatteria());
            ps.setFloat(2, log.getTensioneVolt());
            ps.setFloat(3, log.getCorrenteProdottaMa());
            ps.setFloat(4, log.getCorrenteConsumataMa());
            
            if (log.getErroreRilevato() != null) {
                ps.setString(5, log.getErroreRilevato());
            } else {
                ps.setNull(5, Types.VARCHAR);
            }
            
            ps.executeUpdate();
            System.out.println("[LogEnergiaDAO] Nuovo record energetico inserito con successo.");
        } catch (SQLException e) {
            System.err.println("[LogEnergiaDAO ERROR] Errore durante l'inserimento del log energetico: " + e.getMessage());
            throw e;
        }
    }
}