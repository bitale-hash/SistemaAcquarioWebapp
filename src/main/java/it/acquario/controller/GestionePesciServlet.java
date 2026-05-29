package it.acquario.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import it.acquario.dao.PesceDAO;
import it.acquario.model.Pesce;
import it.acquario.util.DBConnessione;

@WebServlet("/GestionePesciServlet")
public class GestionePesciServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PesceDAO dao = new PesceDAO();
        HttpSession session = request.getSession();
        String azione = request.getParameter("azione");

        // Anti-cache headers
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        try {
            if (azione != null) {
                if (azione.equals("elimina")) {
                    int id = Integer.parseInt(request.getParameter("id"));
                    if (dao.eliminaPesce(id)) {
                        session.setAttribute("successo", "Specie rimossa correttamente.");
                        // Ricalcola automaticamente i parametri ottimali della vasca dopo l'eliminazione
                        aggiornaSoglieEcosistema();
                    }
                    response.sendRedirect("GestionePesciServlet");
                    return;
                } 
                else if (azione.equals("preparaModifica")) {
                    int id = Integer.parseInt(request.getParameter("id"));
                    Pesce p = dao.getPesceById(id);
                    request.setAttribute("pesceDaModificare", p);
                }
            }
            
            // Visualizzazione standard
            List<Pesce> lista = dao.getAllPesci();
            request.setAttribute("listaPesci", lista);
            request.getRequestDispatcher("gestione_pesci.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("DashboardServlet");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PesceDAO dao = new PesceDAO();
        HttpSession session = request.getSession();

        try {
            // Recuperiamo tutti i parametri (compresi i nuovi pH)
            int id = Integer.parseInt(request.getParameter("idPesce")); 
            String specie = request.getParameter("specie");
            String tipoCibo = request.getParameter("tipoCibo");
            double tMin = Double.parseDouble(request.getParameter("tempMin"));
            double tMax = Double.parseDouble(request.getParameter("tempMax"));
            float phMin = Float.parseFloat(request.getParameter("phMin")); // <-- AGGIUNTO
            float phMax = Float.parseFloat(request.getParameter("phMax")); // <-- AGGIUNTO
            int maxMc = Integer.parseInt(request.getParameter("numMaxMc"));
            String stress = request.getParameter("segnaliStress");

            // Costruiamo l'oggetto Pesce (Assicurati che il costruttore accetti anche phMin e phMax, o usa i setter)
            Pesce p = new Pesce();
            p.setIdPesce(id);
            p.setSpecie(specie);
            p.setTipoCibo(tipoCibo);
            p.setTempMin(tMin);
            p.setTempMax(tMax);
            p.setPhMin(phMin); // <-- AGGIUNTO
            p.setPhMax(phMax); // <-- AGGIUNTO
            p.setNumMaxMc(maxMc);
            p.setSegnaliStress(stress);

            boolean esito;
            if (id > 0) {
                // Aggiornamento
                esito = dao.updatePesce(p);
                if(esito) session.setAttribute("successo", "Specie aggiornata con successo.");
            } else {
                // Inserimento
                esito = dao.insertPesce(p);
                if(esito) session.setAttribute("successo", "Nuova specie aggiunta.");
            }
            
            // Se l'inserimento o la modifica hanno avuto successo, sincronizziamo le soglie dell'ESP32
            if (esito) {
                aggiornaSoglieEcosistema();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errore", "Errore nei dati inseriti.");
        }

        response.sendRedirect("GestionePesciServlet");
    }

    /**
     * Calcola l'intersezione biologica sicura di tutti i pesci in vasca
     * e aggiorna istantaneamente la tabella configurazione per l'ESP32.
     */
    private void aggiornaSoglieEcosistema() throws SQLException {
        // 1. Trova l'intersezione perfetta: i minimi più alti e i massimi più bassi di tutti i pesci in vasca
        String sqlCalcolo = "SELECT MAX(temp_min) AS t_min, MIN(temp_max) AS t_max, MAX(ph_min) AS p_min, MIN(ph_max) AS p_max FROM pesci";
        
        // 2. Query di aggiornamento basata sulla colonna "parametro" (Chiave primaria)
        String sqlUpdateConfig = "UPDATE configurazione SET valore = ? WHERE parametro = ?";

        try (Connection conn = DBConnessione.getConnessione();
             PreparedStatement psCalcolo = conn.prepareStatement(sqlCalcolo);
             ResultSet rs = psCalcolo.executeQuery()) {
            
            if (rs.next()) {
                double tempMinOttimale = rs.getDouble("t_min");
                double tempMaxOttimale = rs.getDouble("t_max");
                float phMinOttimale = rs.getFloat("p_min");
                float phMaxOttimale = rs.getFloat("p_max");

                // Se l'aggregazione restituisce valori validi (tabella pesci non vuota)
                if (tempMinOttimale > 0) {
                    try (PreparedStatement psUpdate = conn.prepareStatement(sqlUpdateConfig)) {
                        
                        // Allineato alle CHIAVI REALI del tuo Database (Vedi screenshot):
                        
                        // 1. Aggiorna la soglia minima del pH
                        psUpdate.setString(1, String.valueOf(phMinOttimale));
                        psUpdate.setString(2, "ph_min"); 
                        psUpdate.addBatch();
                        
                        // 2. Aggiorna la soglia massima del pH
                        psUpdate.setString(1, String.valueOf(phMaxOttimale));
                        psUpdate.setString(2, "ph_max"); 
                        psUpdate.addBatch();

                        // 3. Aggiorna la soglia minima della temperatura
                        psUpdate.setString(1, String.valueOf(tempMinOttimale));
                        psUpdate.setString(2, "temp_min"); 
                        psUpdate.addBatch();

                        // 4. Aggiorna la soglia massima della temperatura
                        psUpdate.setString(1, String.valueOf(tempMaxOttimale));
                        psUpdate.setString(2, "temp_max"); 
                        psUpdate.addBatch();

                        // Esegue tutte e 4 le operazioni in un colpo solo
                        psUpdate.executeBatch();
                    }
                }
            }
        }
    }
}