package it.acquario.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import it.acquario.dao.ComponenteDAO;
import it.acquario.dao.AllarmeDAO;
import it.acquario.model.Componente;
import it.acquario.model.LivelloAllarme;

@WebServlet("/GestioneComponentiServlet")
public class GestioneComponentiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
 
    /**
     * OPERAZIONI DI SOLA LETTURA O AZIONI VIA LINK (GET)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String azione = request.getParameter("azione");
        ComponenteDAO dao = new ComponenteDAO();
        HttpSession session = request.getSession();

        try {
            // ==========================================
            // 1.  ELIMINA COMPONENTE
            // ==========================================
            if ("elimina".equals(azione)) {
                String idParam = request.getParameter("id");
                if (idParam != null && !idParam.isEmpty()) {
                    int id = Integer.parseInt(idParam);
                    Componente comp = dao.getComponenteById(id);
                    String nomeComp = (comp != null) ? comp.getNome() : "Componente";

                    if (dao.eliminaComponente(id)) {
                        session.setAttribute("successo", "🗑️ **" + nomeComp + "** eliminato correttamente dall'inventario.");
                    } else {
                        session.setAttribute("errore", "Impossibile eliminare il componente selezionato.");
                    }
                }
                response.sendRedirect("GestioneComponentiServlet");
                return;

            // ==========================================
            // 2.  MANUTENZIONE
            // ==========================================
            } else if ("manutenzione".equals(azione)) {
                String idParam = request.getParameter("id");
                if (idParam != null && !idParam.isEmpty()) {
                    int id = Integer.parseInt(idParam);
                    Componente comp = dao.getComponenteById(id); 
                    
                    if (comp != null) {
                        if (dao.resetManutenzione(id)) {
                            session.setAttribute("successo", "✅ Manutenzione registrata con successo per: **" + comp.getNome() + "**");
                        } else {
                            session.setAttribute("errore", "❌ Impossibile aggiornare la manutenzione di " + comp.getNome());
                        }
                    } else {
                        session.setAttribute("errore", "Componente non trovato nel database.");
                    }
                }
                
                String referer = request.getHeader("Referer");
                if (referer != null && !referer.isEmpty()) {
                    response.sendRedirect(referer);
                } else {
                    response.sendRedirect(request.getContextPath() + "/DashboardServlet");
                }
                return;

            // ==========================================
            // 3.  MODIFICA COMPONENTE (Carica il Form)
            // ==========================================
            } else if ("modificaForm".equals(azione)) {
                String idParam = request.getParameter("id");
                String from = request.getParameter("from");
                request.setAttribute("provenienza", from);  
                  
                if (idParam != null) {
                    Componente comp = dao.getComponenteById(Integer.parseInt(idParam));
                    request.setAttribute("componente", comp);
                    
                    // MODIFICATO: Rimaniamo sulla pagina principale, ci pensa la modale a comparire!
                    request.getRequestDispatcher("gestione_componenti.jsp").forward(request, response);
                    return;
                }
                response.sendRedirect("GestioneComponentiServlet");
                return;
            }

            // ==========================================
            // VISTA DI DEFAULT: MOSTRA LA TABELLA
            // ==========================================
            List<Componente> tuttiIComponenti = dao.getAllComponenti();
            request.setAttribute("listaCompleta", tuttiIComponenti);
            request.getRequestDispatcher("gestione_componenti.jsp").forward(request, response);

        } catch (Exception e) {
            session.setAttribute("errore", "Errore nel sistema componenti: " + e.getMessage());
            response.sendRedirect("GestioneComponentiServlet");
        }
    }

    /**
     * OPERAZIONI DI SALVATAGGIO / SCRITTURA DATI (POST)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String azione = request.getParameter("azione");
        ComponenteDAO dao = new ComponenteDAO();
        HttpSession session = request.getSession();
        String stringaReindirizzamento = "GestioneComponentiServlet"; // Fallback di ritorno

        try {
            // ==========================================
            // 4.  AGGIUNGI COMPONENTE
            // ==========================================
            if ("inserisci".equals(azione)) {
                Componente c = new Componente();
                c.setNome(request.getParameter("nome"));
                c.setPrezzo(new java.math.BigDecimal(request.getParameter("prezzo")));
                c.setData_acquisto(java.sql.Date.valueOf(request.getParameter("data_acquisto")));
                c.setManutenzione_ogni_giorni(Integer.parseInt(request.getParameter("manutenzione_ogni_giorni")));
                c.setVita_media_mesi(Integer.parseInt(request.getParameter("vita_media_mesi")));
                c.setNote(request.getParameter("note"));
                c.setFoto_scontrino_path(request.getParameter("foto_path"));
                
                String dataM = request.getParameter("ultima_manutenzione");
                c.setUltima_manutenzione((dataM != null && !dataM.isEmpty()) ? java.sql.Date.valueOf(dataM) : new java.sql.Date(System.currentTimeMillis()));

                if (dao.inserisciComponente(c)) {
                    session.setAttribute("successo", "Componente aggiunto con successo!");
                } else {
                    session.setAttribute("errore", "Errore nell'inserimento del componente.");
                }

            // ==========================================
            // 5.  SALVA MODIFICA COMPONENTE (INTEGRATO)
            // ==========================================
            } else if ("aggiorna".equals(azione)) {
                int id = Integer.parseInt(request.getParameter("id"));
                String nome = request.getParameter("nome");
                java.math.BigDecimal prezzo = new java.math.BigDecimal(request.getParameter("prezzo"));
                java.sql.Date dataAcquisto = java.sql.Date.valueOf(request.getParameter("data_acquisto"));
                java.sql.Date ultimaManutenzione = java.sql.Date.valueOf(request.getParameter("ultima_manutenzione"));
                int manutenzioneGiorni = Integer.parseInt(request.getParameter("manutenzione_ogni_giorni"));
                int vitaMesi = Integer.parseInt(request.getParameter("vita_media_mesi"));
                String fotoPath = request.getParameter("foto_path");
                String note = request.getParameter("note");
                boolean rotto = request.getParameter("rotto") != null;
                boolean difettoso = request.getParameter("difettoso") != null;
                
                Componente c = new Componente();
                c.setId_componente(id);
                c.setNome(nome);
                c.setPrezzo(prezzo);
                c.setData_acquisto(dataAcquisto);
                c.setManutenzione_ogni_giorni(manutenzioneGiorni);
                c.setVita_media_mesi(vitaMesi);
                c.setFoto_scontrino_path(fotoPath);
                c.setNote(note);
                c.setUltima_manutenzione(ultimaManutenzione);
                c.setRotto(rotto);
                c.setDifettoso(difettoso);

                // Gestione della provenienza del form per decidere il redirect finale
                String from = request.getParameter("from");
                if ("dashboard".equals(from)) {
                    stringaReindirizzamento = "DashboardServlet";
                }

                // Eseguiamo l'update mappato sul tuo DAO
                if (dao.updateComponente(c)) {
                    session.setAttribute("successo", "Componente aggiornato correttamente!");
                    
                    // Allineamento automatico del registro Allarmi/Notifiche
                    AllarmeDAO allarmeDao = new AllarmeDAO();
                    if (c.isRotto()) {
                        String msg = "Allarme: il componente '" + c.getNome() + "' risulta guasto o rotto.";
                        allarmeDao.aggiornaStatoAllarme(c.getId_componente(), true, msg, LivelloAllarme.CRITICAL);
                    } else if (c.isDifettoso()) {
                        String msg = "Attenzione: il componente '" + c.getNome() + "' sta mostrando anomalie di funzionamento.";
                        allarmeDao.aggiornaStatoAllarme(c.getId_componente(), true, msg, LivelloAllarme.WARNING);
                    } else {
                        String msg = "Il componente '" + c.getNome() + "' è tornato a funzionare normalmente.";
                        allarmeDao.aggiornaStatoAllarme(c.getId_componente(), false, msg, LivelloAllarme.INFO);
                    }
                } else {
                    session.setAttribute("errore", "Impossibile aggiornare il database.");
                }
            }

        } catch (Exception e) {
            session.setAttribute("errore", "Dati inseriti non validi o errore sistema: " + e.getMessage());
        }

        // Redirect dinamico basato sul form di provenienza
        response.sendRedirect(stringaReindirizzamento);
    }
}