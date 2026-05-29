package it.acquario.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import it.acquario.dao.AllarmeDAO;
import it.acquario.model.Allarme;
import it.acquario.model.LivelloAllarme; // Assicurati che l'import dell'Enum sia corretto

@WebServlet("/StoricoAllarmiServlet")
public class StoricoAllarmiServlet extends HttpServlet {
    
    // Il doGet si occupa ESCLUSIVAMENTE di mostrare i dati (Lettura)
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AllarmeDAO dao = new AllarmeDAO();
        
        // --- 1. SICUREZZA ANTI-CACHE ---
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setDateHeader("Expires", 0); // Proxies
        
        try {
            // Carichiamo i dati dal DAO mantenendo in memoria le modifiche strutturali fatte prima
            List<Allarme> storico = dao.getStoricoAllarmi();
            request.setAttribute("listaStoricoAllarmi", storico);
            
            // Inoltro alla pagina di visualizzazione
            request.getRequestDispatcher("storico_allarmi.jsp").forward(request, response);
            
        } catch (SQLException e) {
            e.printStackTrace();
            HttpSession session = request.getSession();
            session.setAttribute("errore", "Errore nel recupero dello storico allarmi.");
            response.sendRedirect("DashboardServlet");
        }
    }

    // Il doPost si occupa di gestire le AZIONI che modificano il DB (Scrittura)
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AllarmeDAO dao = new AllarmeDAO();
        HttpSession session = request.getSession();
        String azione = request.getParameter("azione");
        String provenienza = request.getParameter("from");
        
        try {
            boolean azioneEseguita = false;

            if (azione != null) {
                if (azione.equals("leggiTutto")) {
                    if (dao.segnaTuttiComeLetti()) {
                        session.setAttribute("successo", "Tutti gli allarmi sono stati segnati come letti.");
                    }
                    azioneEseguita = true;
                    
                } else if (azione.equals("leggiSingolo")) {
                    String idStr = request.getParameter("id");
                    if (idStr != null) {
                        int id = Integer.parseInt(idStr);
                        if (dao.segnaComeLetto(id)) {
                            session.setAttribute("successo", "Allarme archiviato correttamente.");
                        }
                    }
                    azioneEseguita = true;
                    
                } else if (azione.equals("risolviSingolo")) { 
                    String idStr = request.getParameter("id");
                    if (idStr != null) {
                        int id = Integer.parseInt(idStr);
                        if (dao.risolviAllarmeManualmente(id)) {
                            session.setAttribute("successo", "Allarme contrassegnato come risolto.");
                        }
                    }
                    azioneEseguita = true;

                // ==========================================
                //  AZIONI CRUD 
                // ==========================================
                
                } else if (azione.equals("inserisci")) {
                    String livelloStr = request.getParameter("livello");
                    String dataOraStr = request.getParameter("data_ora");
                    String messaggio = request.getParameter("messaggio");
                    String idCompStr = request.getParameter("id_componente_rif");
                    boolean risolto = request.getParameter("risolto") != null;
                    boolean letta = request.getParameter("letta") != null;
                    
                    // Log di debug in console Tomcat per verificare cosa arriva dalla pagina
                    System.out.println("DEBUG INSERISCI -> Livello: " + livelloStr + ", Data: " + dataOraStr + ", Msg: " + messaggio + ", IdComp: " + idCompStr);
                    
                    if (livelloStr != null && dataOraStr != null && messaggio != null && idCompStr != null) {
                        Allarme nuovo = new Allarme();
                        nuovo.setLivello(LivelloAllarme.valueOf(livelloStr.toUpperCase()));
                        
                        String dataConvertita = dataOraStr.replace("T", " ");
                        if(dataConvertita.length() == 16) dataConvertita += ":00";
                        java.util.Date dataParsed = java.sql.Timestamp.valueOf(dataConvertita);
                        nuovo.setData_ora((Timestamp) dataParsed);
                        
                        nuovo.setMessaggio(messaggio);
                        nuovo.setId_componente_rif(Integer.parseInt(idCompStr));
                        nuovo.setRisolto(risolto);
                        nuovo.setLetta(letta);
                        
                        if (dao.inserisciAllarme(nuovo)) {
                            session.setAttribute("successo", "Nuovo allarme registrato correttamente.");
                        } else {
                            session.setAttribute("errore", "Errore: Database rifiuta l'inserimento.");
                        }
                    }
                    azioneEseguita = true;

                } else if (azione.equals("modifica")) {
                    String idStr = request.getParameter("id");
                    String livelloStr = request.getParameter("livello");
                    String dataOraStr = request.getParameter("data_ora");
                    String messaggio = request.getParameter("messaggio");
                    String idCompStr = request.getParameter("id_componente_rif");
                    boolean risolto = request.getParameter("risolto") != null;
                    boolean letta = request.getParameter("letta") != null;
                    
                    System.out.println("DEBUG MODIFICA -> ID: " + idStr + ", Livello: " + livelloStr + ", Data: " + dataOraStr);
                    
                    if (idStr != null && livelloStr != null && dataOraStr != null && messaggio != null && idCompStr != null) {
                        Allarme modificato = new Allarme();
                        modificato.setId_allarme(Integer.parseInt(idStr)); // Questo ID serve per il WHERE
                        modificato.setLivello(LivelloAllarme.valueOf(livelloStr.toUpperCase()));
                        
                        String dataConvertita = dataOraStr.replace("T", " ");
                        if(dataConvertita.length() == 16) dataConvertita += ":00";
                        java.util.Date dataParsed = java.sql.Timestamp.valueOf(dataConvertita);
                        modificato.setData_ora((Timestamp) dataParsed);
                        
                        modificato.setMessaggio(messaggio);
                        modificato.setId_componente_rif(Integer.parseInt(idCompStr));
                        modificato.setRisolto(risolto);
                        modificato.setLetta(letta);
                        
                        if (dao.aggiornaAllarme(modificato)) {
                            session.setAttribute("successo", "Allarme modificato correttamente.");
                        } else {
                            session.setAttribute("errore", "Modifica fallita: ID Allarme " + idStr + " non trovato.");
                        }
                    }
                    azioneEseguita = true;

                } else if (azione.equals("elimina")) {
                    String idStr = request.getParameter("id");
                    System.out.println("DEBUG ELIMINA -> ID: " + idStr);
                    
                    if (idStr != null) {
                        int id = Integer.parseInt(idStr);
                        if (dao.eliminaAllarme(id)) {
                            session.setAttribute("successo", "Allarme rimosso con successo dallo storico.");
                        } else {
                            session.setAttribute("errore", "Eliminazione fallita: ID Allarme non trovato.");
                        }
                    }
                    azioneEseguita = true;
                }
            }

            // --- GESTIONE REINDIRIZZAMENTO (Pattern PRG) ---
            if (azioneEseguita) {
                if ("dashboard".equals(provenienza)) {
                    response.sendRedirect("DashboardServlet");
                } else {
                    response.sendRedirect("StoricoAllarmiServlet");
                }
            } else {
                response.sendRedirect("StoricoAllarmiServlet");
            }
            
        } catch (NumberFormatException e) {
            e.printStackTrace();
            session.setAttribute("errore", "Errore Formato Numerico: " + e.getMessage());
            response.sendRedirect("StoricoAllarmiServlet");
        } catch (SQLException e) {
            e.printStackTrace();
            session.setAttribute("errore", "Errore Database SQL: " + e.getMessage());
            response.sendRedirect("StoricoAllarmiServlet");
        } catch (Exception e) {
            e.printStackTrace();
            // QUESTO CI DIRÀ L'ERRORE REALE (es. la data, un campo null, un enum sbagliato)
            session.setAttribute("errore", "Errore Generico: " + e.toString()); 
            response.sendRedirect("StoricoAllarmiServlet");
        }
    }
}