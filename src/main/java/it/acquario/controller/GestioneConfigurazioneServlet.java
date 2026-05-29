package it.acquario.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import it.acquario.dao.ConfigurazioneDAO;
import it.acquario.model.Configurazione;
import it.acquario.model.Categoria;
import it.acquario.model.TipoDato;

@WebServlet("/GestioneConfigurazioneServlet")
public class GestioneConfigurazioneServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ConfigurazioneDAO dao = new ConfigurazioneDAO();

    // 1. IL GET GESTISCE LA LETTURA DELLA TABELLA, L'APERTURA FORM (MATITA) E L'ELIMINAZIONE (LINK)
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String azione = request.getParameter("azione");
        HttpSession session = request.getSession();
        
        try {
            // Se l'utente clicca sulla matita per modificare, carichiamo la singola configurazione
            if ("modificaForm".equals(azione)) {
                String parametroId = request.getParameter("id");
                if (parametroId != null) {
                    Configurazione config = dao.getConfigByParametro(parametroId);
                    request.setAttribute("configurazioneSelezionata", config);
                }
            }
            // L'eliminazione scatta da un link <a>, quindi viaggia in GET!
            else if ("elimina".equals(azione)) {
                String parametroDaEliminare = request.getParameter("id");
                if (parametroDaEliminare != null) {
                    dao.eliminaConfigurazione(parametroDaEliminare); 
                    session.setAttribute("successo", "🗑️ Parametro rimosso con successo!");
                }
                response.sendRedirect("GestioneConfigurazioneServlet");
                return;
            }

            // Carichiamo sempre la lista totale per riempire la tabella del CRUD
            List<Configurazione> listaCompleta = dao.getAllConfigurazioni(); 
            request.setAttribute("listaConfigurazioni", listaCompleta);
            
            request.getRequestDispatcher("gestione_configurazioni.jsp").forward(request, response);
            
        } catch (SQLException e) {
            e.printStackTrace();
            session.setAttribute("errore", "❌ Errore nel caricamento o rimozione dei dati di configurazione.");
            response.sendRedirect("DashboardServlet");
        }
    }

    // 2. IL POST GESTISCE SOLO I SALVATAGGI DEI FORM (DASHBOARD E MODALI)
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String azione = request.getParameter("azione");
        HttpSession session = request.getSession();

        try {
            // RAMO 1: SINCRONIZZAZIONE DI MASSA DALLA DASHBOARD ESP32 (azione è null)
            if (azione == null) { 
                Enumeration<String> parameterNames = request.getParameterNames();
                boolean erroreValidazione = false;
                StringBuilder messaggiErrore = new StringBuilder();

                while (parameterNames.hasMoreElements()) {
                    String nomeParametro = parameterNames.nextElement();
                    String valoreInviatoStr = request.getParameter(nomeParametro);
                    
                    Configurazione conf = dao.getConfigByParametro(nomeParametro);
                    
                    if (conf != null) {
                        try {
                            float valoreInviato = Float.parseFloat(valoreInviatoStr.replace(",", "."));
                            valoreInviatoStr = String.valueOf(valoreInviato);	
                            
                            if (valoreInviato < conf.getMinimo() || valoreInviato > conf.getMassimo()) {
                                erroreValidazione = true;
                                messaggiErrore.append("⚠️ ").append(conf.getDescrizione())
                                              .append(": valore fuori range (")
                                              .append(conf.getMinimo()).append(" - ")
                                              .append(conf.getMassimo()).append(")<br>");
                                continue; 
                            }
                        } catch (NumberFormatException nfe) {
                            // Salta validazione numerica per stringhe
                        }
                        
                        // RICICLATO: Iniettiamo il nuovo valore dentro l'oggetto completo estratto dal DB
                        conf.setValore(valoreInviatoStr);
                        
                        // E passiamo tutto l'oggetto all'unico metodo di Update del DAO!
                        dao.updateConfigurazioneCompleta(conf);
                    }
                }
                
                if (erroreValidazione) {
                    session.setAttribute("errore", "<b>Alcuni dati non sono validi:</b><br>" + messaggiErrore.toString());
                } else {
                    session.setAttribute("successo", "✅ Tutte le configurazioni sono state salvate!");
                }
                
                response.sendRedirect("DashboardServlet");
                return;
            }

            // RAMO 2: OPERAZIONI CRUD DELLA PAGINA GESTIONE_CONFIGURAZIONI (INSERISCI E AGGIORNA)
            if ("inserisci".equals(azione)) {
                String parametro = request.getParameter("parametro");
                String descrizione = request.getParameter("descrizione");
                String valore = request.getParameter("valore");
                String categoriaStr = request.getParameter("categoria");
                String tipoDatoStr = request.getParameter("tipo_dato");

                Configurazione nuova = new Configurazione();
                nuova.setParametro(parametro);
                nuova.setDescrizione(descrizione);
                nuova.setValore(valore);
                nuova.setCategoria(Categoria.valueOf(categoriaStr.toUpperCase()));
                nuova.setTipoDato(TipoDato.valueOf(tipoDatoStr.toUpperCase()));
                
                dao.inserisciConfigurazione(nuova); 
                session.setAttribute("successo", "✅ Nuovo parametro inserito con successo!");

            } else if ("aggiorna".equals(azione)) {
                String parametro = request.getParameter("parametro");
                String descrizione = request.getParameter("descrizione");
                String valore = request.getParameter("valore");
                String categoriaStr = request.getParameter("categoria");
                String tipoDatoStr = request.getParameter("tipo_dato");

                Configurazione daAggiornare = new Configurazione();
                daAggiornare.setParametro(parametro);
                daAggiornare.setDescrizione(descrizione);
                daAggiornare.setValore(valore);
                daAggiornare.setCategoria(Categoria.valueOf(categoriaStr.toUpperCase()));
                daAggiornare.setTipoDato(TipoDato.valueOf(tipoDatoStr.toUpperCase()));
                
                dao.updateConfigurazioneCompleta(daAggiornare);
                session.setAttribute("successo", "✅ Configurazione salvata e metadati aggiornati!");
            }

            response.sendRedirect("GestioneConfigurazioneServlet");

        } catch (SQLException e) {
            e.printStackTrace();
            session.setAttribute("errore", "❌ Errore del database durante l'operazione.");
            response.sendRedirect("GestioneConfigurazioneServlet");
        }
    }
}