package it.acquario.dao;

import java.sql.Connection;
import java.lang.Enum;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.acquario.model.Allarme;
import it.acquario.model.LivelloAllarme;
import it.acquario.util.DBConnessione;

public class AllarmeDAO {

    
   public List<Allarme> getStoricoAllarmi() throws SQLException {
	    List<Allarme> lista = new ArrayList<>();
	    // Usiamo una JOIN per prendere il nome del componente dalla tabella componenti_fisici
	    /*String query = "SELECT a.*, c.nome AS nome_comp " +
	                   "FROM notifiche_allarmi a " +
	                   "LEFT JOIN componenti_fisici c ON a.id_componente_rif = c.id_componente " +
	                   "ORDER BY a.data_ora DESC";*/
	    String query = "SELECT a.*, c.nome AS nome_comp " +
                "FROM notifiche_allarmi a " +
                "LEFT JOIN componenti_fisici c ON a.id_componente_rif = c.id_componente " +
                "ORDER BY a.data_ora DESC";
	    try (Connection conn = DBConnessione.getConnessione();
	         PreparedStatement ps = conn.prepareStatement(query);
	         ResultSet rs = ps.executeQuery()) {
	        while (rs.next()) {
	        	Allarme a = new Allarme();
	            a.setId_allarme(rs.getInt("id_allarme"));
	            
	            String livelloString = rs.getString("livello");
                if (livelloString != null) {
                    a.setLivello(LivelloAllarme.valueOf(livelloString.toUpperCase().trim()));
                }
	            
	            a.setMessaggio(rs.getString("messaggio"));
	            a.setLetta(rs.getBoolean("letta"));      
	            a.setData_ora(rs.getTimestamp("data_ora"));  
	            a.setId_componente_rif(rs.getInt("id_componente_rif"));  
	            a.setRisolto(rs.getBoolean("risolto"));    
	            a.setNomeComponente(rs.getString("nome_comp")); 
	            lista.add(a);
	        }
	    }
	    return lista;
	}
    
   public List<Allarme> getUltimiTreAllarmi() throws SQLException {
        List<Allarme> lista = new ArrayList<>();
        // Query che recupera solo i 5 più recenti
        String query = "SELECT * FROM notifiche_allarmi ORDER BY data_ora DESC LIMIT 5";

        try (Connection conn = DBConnessione.getConnessione();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Allarme a = new Allarme();
                a.setId_allarme(rs.getInt("id_allarme"));
                
                String livelloString = rs.getString("livello");
                if (livelloString != null) {
                    a.setLivello(LivelloAllarme.valueOf(livelloString.toUpperCase().trim()));
                }
	            
                a.setMessaggio(rs.getString("messaggio"));
                a.setLetta(rs.getBoolean("letta"));
                a.setData_ora(rs.getTimestamp("data_ora"));
                lista.add(a);
            }
        }
        return lista;
   }
   
   public boolean segnaComeLetto(int id) {
	    String sql = "UPDATE notifiche_allarmi SET letta = 1 WHERE id_allarme = ?";
	    try (Connection conn = DBConnessione.getConnessione();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setInt(1, id);
	        return ps.executeUpdate() > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}

	public boolean segnaTuttiComeLetti() {
	    String sql = "UPDATE notifiche_allarmi SET letta = 1 WHERE letta = 0";
	    try (Connection conn = DBConnessione.getConnessione();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        return ps.executeUpdate() >= 0; // Restituisce true anche se c'erano 0 allarmi da leggere
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	public int countNonLetti() {
	    int count = 0;
	    String query = "SELECT COUNT(*) FROM notifiche_allarmi WHERE letta = 0";
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
	 
	public boolean aggiornaStatoAllarme(int idComponente, boolean isErrore, String messaggio, LivelloAllarme livello) {
	    // Se isErrore è true: l'allarme è attivo (risolto = 0, letta = 0)
	    // Se isErrore è false: l'allarme rientra (risolto = 1, la contrassegniamo come letta o lasciamo 0 a seconda di come preferisci sulla Dashboard)
	    int flagRisolto = isErrore ? 0 : 1;
	    int flagLetta = isErrore ? 0 : 1; // Se vuoi che rimanga da leggere anche se risolto, metti a 0!
	    
	    // QUERY ALLARME DINAMICA
	    // Se c'è un errore, creiamo/aggiorniamo l'allarme attivo per quel componente.
	    // Se si sta risolvendo, chiudiamo SOLO l'allarme di quel componente che risulta ancora aperto (risolto = 0)!
	    String sqlAllarme = isErrore 
	        ? "UPDATE notifiche_allarmi SET risolto = ?, letta = ?, messaggio = ?, livello = ?, data_ora = NOW() WHERE id_componente_rif = ?"
	        : "UPDATE notifiche_allarmi SET risolto = ?, letta = ?, data_ora = NOW() WHERE id_componente_rif = ? AND risolto = 0";
	                 
	    String sqlComponente = "UPDATE componenti_fisici SET rotto = ?, difettoso = ? WHERE id_componente = ?";
	    
	    try (Connection conn = DBConnessione.getConnessione()) {
	        conn.setAutoCommit(false);
	        
	        // --- PARTE 1: AGGIORNAMENTO NOTIFICA ALLARME ---
	        try (PreparedStatement ps = conn.prepareStatement(sqlAllarme)) {
	            ps.setInt(1, flagRisolto); // risolto
	            ps.setInt(2, flagLetta);   // letta
	            
	            if (isErrore) {
	                ps.setString(3, messaggio);
	                ps.setString(4, livello != null ? livello.name() : "INFO");
	                ps.setInt(5, idComponente);
	            } else {
	                ps.setInt(3, idComponente);
	            }
	            ps.executeUpdate(); 
	        }
	        
	        // --- PARTE 2: AGGIORNAMENTO STATO COMPONENTE ---
	        try (PreparedStatement psComp = conn.prepareStatement(sqlComponente)) {
	            int flagRotto = 0;
	            int flagDifettoso = 0;
	            
	            if (isErrore && livello != null) {
	                switch (livello) {
	                    case CRITICAL:
	                        flagRotto = 1;
	                        break;
	                    case WARNING:
	                        flagDifettoso = 1;
	                        break;
	                    default:
	                        break;
	                }
	            }
	            
	            psComp.setInt(1, flagRotto);
	            psComp.setInt(2, flagDifettoso);
	            psComp.setInt(3, idComponente);
	            psComp.executeUpdate(); 
	        }
	        
	        conn.commit(); 
	        return true;
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	public boolean aggiornaRisoluzione(int idAllarme, boolean risolto) {
	    // Se risolviamo (risolto=1), allora segnamo anche come letto automaticamente
	    // Se l'allarme si riattiva (risolto=0), allora torna non letto
	    int letto = risolto ? 1 : 0; 
	    
	    String sql = "UPDATE notifiche_allarmi SET risolto = ?, letta = ?, data_ora = NOW() WHERE id_allarme = ?";
	    try (Connection conn = DBConnessione.getConnessione();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setBoolean(1, risolto);
	        ps.setInt(2, letto);
	        ps.setInt(3, idAllarme);
	        return ps.executeUpdate() > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	public boolean risolviAllarmeManualmente(int idAllarme) {
	    // 1. Query per aggiornare la notifica (la imposta come risolta, letta e cambia il messaggio)
	    String sqlAllarme = "UPDATE notifiche_allarmi SET risolto = 1, letta = 1, data_ora = NOW(), " +
	                        "messaggio = 'Problema risolto manualmente dall''utente' WHERE id_allarme = ?";
	    
	    // 2. Query per riparare il componente (estraiamo l'id_componente_rif associato a questo allarme e lo azzeriamo)
	    String sqlComponente = "UPDATE componenti_fisici SET rotto = 0, difettoso = 0 " +
	                           "WHERE id_componente = (SELECT id_componente_rif FROM notifiche_allarmi WHERE id_allarme = ?)";
	    
	    try (Connection conn = DBConnessione.getConnessione()) {
	        // Disabilitiamo l'autocommit per fare una transazione sicura a due step
	        conn.setAutoCommit(false);
	        
	        // STEP 1: Aggiorniamo la tabella delle notifiche
	        try (PreparedStatement psAllarme = conn.prepareStatement(sqlAllarme)) {
	            psAllarme.setInt(1, idAllarme);
	            psAllarme.executeUpdate();
	        }
	        
	        // STEP 2: Aggiorniamo la tabella componenti azzerando i flag rotto/difettoso
	        try (PreparedStatement psComp = conn.prepareStatement(sqlComponente)) {
	            psComp.setInt(1, idAllarme); // Passiamo l'id allarme, la sotto-query SQL (SELECT ...) troverà il componente giusto
	            psComp.executeUpdate();
	        }
	        
	        // Confermiamo entrambe le operazioni sul Database
	        conn.commit();
	        return true;
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	// ==========================================
	// METODI CRUD  
	// ==========================================

	public boolean inserisciAllarme(Allarme allarme) throws SQLException {
	    // RIMOSSO id_allarme: ci pensa il database essendo Auto Increment
	    String sql = "INSERT INTO notifiche_allarmi (livello, data_ora, messaggio, id_componente_rif, risolto, letta) VALUES (?, ?, ?, ?, ?, ?)";
	    
	    try (java.sql.Connection conn = DBConnessione.getConnessione();  
	         java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
	        
	        ps.setString(1, allarme.getLivello().name());
	        ps.setTimestamp(2, new java.sql.Timestamp(allarme.getData_ora().getTime()));
	        ps.setString(3, allarme.getMessaggio());
	        ps.setInt(4, allarme.getId_componente_rif());
	        ps.setBoolean(5, allarme.isRisolto());
	        ps.setBoolean(6, allarme.isLetta());
	        
	        return ps.executeUpdate() > 0;
	    }
	}

	public boolean aggiornaAllarme(Allarme allarme) throws SQLException {
	    // L'id_allarme serve ESCLUSIVAMENTE nella clausola WHERE per identificare il record
	    String sql = "UPDATE notifiche_allarmi SET livello = ?, data_ora = ?, messaggio = ?, id_componente_rif = ?, risolto = ?, letta = ? WHERE id_allarme = ?";
	    
	    try (java.sql.Connection conn = DBConnessione.getConnessione();  
	         java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
	        
	        ps.setString(1, allarme.getLivello().name());
	        ps.setTimestamp(2, new java.sql.Timestamp(allarme.getData_ora().getTime()));
	        ps.setString(3, allarme.getMessaggio());
	        ps.setInt(4, allarme.getId_componente_rif());
	        ps.setBoolean(5, allarme.isRisolto());
	        ps.setBoolean(6, allarme.isLetta());
	        ps.setInt(7, allarme.getId_allarme()); // Mappatura del WHERE
	        
	        return ps.executeUpdate() > 0;
	    }
	}

	public boolean eliminaAllarme(int idAllarme) throws SQLException {
	    String sql = "DELETE FROM notifiche_allarmi WHERE id_allarme = ?";
	    
	    try (java.sql.Connection conn = DBConnessione.getConnessione(); 
	         java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
	        
	        ps.setInt(1, idAllarme);
	        
	        return ps.executeUpdate() > 0;
	    }
	}
}