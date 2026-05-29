package it.acquario.dao;

import it.acquario.model.LogAmbientale;
import it.acquario.util.DBConnessione;
import it.acquario.model.DatiGraficoSerra;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

// Ipotizzo che tu abbia una classe per la connessione, es: it.acquario.util.ConnessioneDB;

public class LogAmbientaleDAO {

    // --- METODO 1: Per il Grafico "Salute Acquario" (pH, Temperatura, data_ora) ---
    public List<LogAmbientale> getListaGraficoAcquario() {
        List<LogAmbientale> lista = new ArrayList<>();
        // Prendiamo gli ultimi 7 record in cui temperatura o pH non sono nulli, ordinati dal più vecchio al più recente
        String query = "SELECT data_ora, temperatura_vasca, ph_vasca FROM (" +
                       "  SELECT data_ora, temperatura_vasca, ph_vasca FROM log_ambientale " +
                       "  WHERE temperatura_vasca IS NOT NULL OR ph_vasca IS NOT NULL " +
                       "  ORDER BY data_ora DESC LIMIT 7" +
                       ") AS sub ORDER BY data_ora ASC";

        try (Connection conn = DBConnessione.getConnessione();// Sostituisci con il tuo metodo di connessione
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                LogAmbientale log = new LogAmbientale();
                log.setDataOra(rs.getTimestamp("data_ora"));
                
                // Gestione dei Double che possono essere NULL nel DB
                double temp = rs.getDouble("temperatura_vasca");
                log.setTemperaturaVasca(rs.wasNull() ? null : temp);
                
                double ph = rs.getDouble("ph_vasca");
                log.setPhVasca(rs.wasNull() ? null : ph);
                
                lista.add(log);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // --- METODO 2: Per il Grafico "Serra"  (litri consumati, energia_prodotta_ma, data_ora)
    public List<DatiGraficoSerra> getListaGraficoSerra() {
        List<DatiGraficoSerra> lista = new ArrayList<>();
        
        // Raggruppiamo per Giorno e Ora (%Y-%m-%d %H). 
        // Usiamo SUM o MAX ma ignorando i record inseriti a 0 dalle tabelle opposte usando dei NULL leggeri.
        String query = "SELECT data_ora_gruppo, " +
                       "       IFNULL(MAX(litri), 0) AS litri_consumati, " +
                       "       IFNULL(MAX(corrente), 0) AS corrente_prodotta_ma " +
                       "FROM ( " +
                       "    (SELECT DATE_FORMAT(data_ora, '%Y-%m-%d %H:00') AS data_ora_gruppo, " +
                       "            ((100 - livello_acqua_vasca_cm) * 10) AS litri, " +
                       "            NULL AS corrente " + // Usiamo NULL invece di 0 per non alterare il MAX dell'altra tabella
                       "     FROM log_ambientale " +
                       "     WHERE livello_acqua_vasca_cm IS NOT NULL " +
                       "     ORDER BY data_ora DESC LIMIT 7) " +
                       "    UNION ALL " +
                       "    (SELECT DATE_FORMAT(data_ora, '%Y-%m-%d %H:00') AS data_ora_gruppo, " +
                       "            NULL AS litri, " + // Usiamo NULL
                       "            corrente_prodotta_ma AS corrente " +
                       "     FROM log_energia " +
                       "     WHERE corrente_prodotta_ma IS NOT NULL " +
                       "     ORDER BY data_ora DESC LIMIT 7) " +
                       ") AS tabella_unita " +
                       "GROUP BY data_ora_gruppo " +
                       "ORDER BY data_ora_gruppo ASC LIMIT 7";

        try (Connection conn = DBConnessione.getConnessione();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // Data_ora_gruppo esce come "2026-05-20 15:00"
                String dataStr = rs.getString("data_ora_gruppo");
                java.sql.Timestamp ts = java.sql.Timestamp.valueOf(dataStr + ":00");

                DatiGraficoSerra dto = new DatiGraficoSerra(
                    ts,
                    rs.getInt("litri_consumati"), 
                    rs.getDouble("corrente_prodotta_ma")
                );
                lista.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return lista;
    }
    
    
    // --- METODI: Per l analizzatoreMessaggi
    public List<Double> getUltimeTemperature(int limit) throws SQLException {
        List<Double> lista = new ArrayList<>();
        // Prendiamo gli ultimi N record ordinati dal più recente, poi invertiamo l'ordine nella query esterna
        String query = "SELECT temperatura_vasca FROM (SELECT data_ora, temperatura_vasca FROM log_ambientale " +
                       "WHERE temperatura_vasca IS NOT NULL ORDER BY data_ora DESC LIMIT ?) AS sub ORDER BY data_ora ASC";
        try (Connection conn = DBConnessione.getConnessione();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(rs.getDouble("temperatura_vasca"));
                }
            }
        }
        return lista;
    }
    
    public List<Double> getUltimiPH(int limit) throws SQLException {
        List<Double> lista = new ArrayList<>();
        String query = "SELECT ph_vasca FROM ( " +
                       "  SELECT data_ora, ph_vasca FROM log_ambientale " +
                       "  WHERE ph_vasca IS NOT NULL " +
                       "  ORDER BY data_ora DESC LIMIT ? " +
                       ") AS sub ORDER BY data_ora ASC";

        try (Connection conn = DBConnessione.getConnessione();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(rs.getDouble("ph_vasca"));
                }
            }
        }
        return lista;
    }
    
    
    
    //CRUD
    public void inserisciLog(LogAmbientale log) throws SQLException {
        String query = "INSERT INTO log_ale (temperatura_vasca, ph_vasca, livello_acqua_vasca_cm, " +
                       "acqua_piante_ml, umidita_terreno, temperatura_aria, umidita_aria, luminosita_lux) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnessione.getConnessione();
             PreparedStatement ps = conn.prepareStatement(query)) {

            // Gestione sicura dei valori Nullable (Double)
            if (log.getTemperaturaVasca() != null) ps.setDouble(1, log.getTemperaturaVasca());
            else ps.setNull(1, Types.DOUBLE);

            if (log.getPhVasca() != null) ps.setDouble(2, log.getPhVasca());
            else ps.setNull(2, Types.DOUBLE);

            // Gestione Integer
            if (log.getLivelloAcquaVascaCm() != null) ps.setInt(3, log.getLivelloAcquaVascaCm());
            else ps.setNull(3, Types.INTEGER);

            if (log.getAcquaPianteMl() != null) ps.setInt(4, log.getAcquaPianteMl());
            else ps.setNull(4, Types.INTEGER);

            if (log.getUmiditaTerreno() != null) ps.setInt(5, log.getUmiditaTerreno());
            else ps.setNull(5, Types.INTEGER);

            // Altri Double
            if (log.getTemperaturaAria() != null) ps.setDouble(6, log.getTemperaturaAria());
            else ps.setNull(6, Types.DOUBLE);

            // Altri Integer
            if (log.getUmiditaAria() != null) ps.setInt(7, log.getUmiditaAria());
            else ps.setNull(7, Types.INTEGER);

            if (log.getLuminositaLux() != null) ps.setInt(8, log.getLuminositaLux());
            else ps.setNull(8, Types.INTEGER);

            ps.executeUpdate();
            System.out.println("[LogAmbientaleDAO] Nuovo log ambientale inserito con successo.");
        } catch (SQLException e) {
            System.err.println("[LogAmbientaleDAO ERROR] Errore nell'inserimento del log ambientale: " + e.getMessage());
            throw e;
        }
    }
}