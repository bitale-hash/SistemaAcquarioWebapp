package it.acquario.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;

import it.acquario.dao.AllarmeDAO;
import it.acquario.dao.ComponenteDAO;
import it.acquario.dao.LogEnergiaDAO;
import it.acquario.model.Utente;

@WebServlet("/DashboardChatbotServlet")
public class DashboardChatbotServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    // Configurazione API Gemini
    private static final String API_KEY = "AIzaSyBmX1zRIWhr4tpr_o59glFOSgLj6iFIMDA";
    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3-flash-preview:generateContent?key=" + API_KEY;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Utente utente = (Utente) session.getAttribute("utente");
        String userMessage = request.getParameter("message"); 
        String nome = (utente != null) ? utente.getUsername() : "ospite";
        String chatHistory = request.getParameter("history");
        
   
       

        if (userMessage == null || userMessage.trim().isEmpty()) return;
        
        String originalMessage = userMessage.trim();
        userMessage = originalMessage.toLowerCase();

        // Inizializzazione DAO
        ComponenteDAO componenteDAO = new ComponenteDAO();
        AllarmeDAO allarmeDAO = new AllarmeDAO();
        LogEnergiaDAO logEnergiaDAO = new LogEnergiaDAO();
        
        String reply = "";

        // --- 1. LOGICA STATICA (Parole Chiave) ---
        String[] intentSaluti = {"ciao", "hey", "buongiorno", "salve", "ehi"};
        String[] intentStato = {"stato_","stato", "situazione", "come va", "problemi", "report"};
        String[] intentEnergia = {"energia_","batteria", "energia", "carica", "corrente"};
        String[] intentGrazie = {"grazie_","grazie", "perfetto", "ottimo", "gentile"};

        if (userMessage.equals("init_welcome_message")) {
            reply = "Ciao <b>" + nome + "</b>! Sono il tuo assistente virtuale. Chiedimi pure 'come va' per un report o fammi domande tecniche sul sistema.";
        } 
        else if (contieneParola(userMessage, intentSaluti)) {
            reply = "Ciao " + nome + "! Come posso esserti utile oggi nella gestione dell'acquario?";
        } 
        else if (userMessage.equals("home_")|| userMessage.equals("Home")|| userMessage.equals("home")) {
        	int rotti = componenteDAO.countRotti();       
            int allarmiAttivi = allarmeDAO.countNonLetti(); 
            int livelloBatteria = logEnergiaDAO.getUltimoLivelloBatteria();

            // Determinazione colori e icone
            String battColor = (livelloBatteria < 30) ? "danger" : (livelloBatteria < 60 ? "warning" : "success");
            String hardwareClass = (rotti > 0) ? "alert-danger" : "alert-light border";
            String hardwareIcon = (rotti > 0) ? "bi-exclamation-triangle-fill" : "bi-check-circle-fill text-success";

            StringBuilder sb = new StringBuilder();
            
            // Header
            sb.append("<div class='mb-3 border-bottom pb-2'>");
            sb.append("  <h6 class='fw-bold text-primary mb-0'><i class='bi bi-house-door-fill me-2'></i>Dashboard Acquaponica</h6>");
            sb.append("  <small class='text-muted'>Benvenuto, ").append(nome).append("</small>");
            sb.append("</div>");

            // Griglia Statistiche (Batteria e Allarmi)
            sb.append("<div class='row g-2 mb-3'>");
            
            // Card Batteria
            sb.append("  <div class='col-6 text-center'>");
            sb.append("    <div class='p-2 rounded bg-white border shadow-sm'>");
            sb.append("      <div class='text-muted' style='font-size: 0.75rem;'>Batteria</div>");
            sb.append("      <div class='fw-bold text-").append(battColor).append("'>");
            sb.append("        <i class='bi bi-lightning-charge-fill'></i> ").append(livelloBatteria).append("%");
            sb.append("      </div>");
            sb.append("    </div>");
            sb.append("  </div>");
            
            // Card Allarmi
            sb.append("  <div class='col-6 text-center'>");
            sb.append("    <div class='p-2 rounded bg-white border shadow-sm'>");
            sb.append("      <div class='text-muted' style='font-size: 0.75rem;'>Allarmi</div>");
            sb.append("      <div class='fw-bold ").append(allarmiAttivi > 0 ? "text-danger" : "text-dark").append("'>");
            sb.append("        <i class='bi bi-bell-fill'></i> ").append(allarmiAttivi);
            sb.append("      </div>");
            sb.append("    </div>");
            sb.append("  </div>");
            
            sb.append("</div>");

            // Banner Stato Hardware
            sb.append("<div class='alert ").append(hardwareClass).append(" py-2 px-3 mb-0' role='alert' style='font-size: 0.85rem;'>");
            sb.append("  <div class='d-flex align-items-center'>");
            sb.append("    <i class='bi ").append(hardwareIcon).append(" me-2 fs-5'></i>");
            sb.append("    <div>");
            sb.append("      <strong>Hardware: ").append(rotti > 0 ? "Intervento richiesto" : "Sistema OK").append("</strong><br>");
            sb.append("      <span>").append(rotti > 0 ? rotti + " componenti guasti rilevati" : "Nessuna anomalia rilevata").append("</span>");
            sb.append("    </div>");
            sb.append("  </div>");
            sb.append("</div>");

            // Call to action
            sb.append("<p class='mt-3 mb-0 small text-muted italic text-center'>Chiedimi consigli sulla gestione o biologia dell'impianto.</p>");

            reply = sb.toString();
        }
        else if (contieneParola(userMessage, intentStato)) {
            reply = generaReportSistema(componenteDAO, allarmeDAO, logEnergiaDAO);
        } 
        else if (contieneParola(userMessage, intentEnergia)) {
            int livello = logEnergiaDAO.getUltimoLivelloBatteria();
            reply = "Il livello attuale della batteria è al <b>" + livello + "%</b>. " + 
                    (livello < 40 ? "Ti consiglio di verificare la fonte di ricarica." : "L'alimentazione è stabile.");
        }
        else if (contieneParola(userMessage, intentGrazie)) {
            String[] ringraziamenti = {"Figurati! Sono qui per questo.", "Di nulla, è un piacere!", "Prego! Se serve altro, chiedi pure."};
            reply = ringraziamenti[new Random().nextInt(ringraziamenti.length)];
        } 
        // --- 2. LOGICA DINAMICA (Gemini AI) ---
        else {
            reply = chiediAI(originalMessage,chatHistory, nome, componenteDAO, allarmeDAO, logEnergiaDAO);
        }

        // Invio risposta al client
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(reply);
    }

    /**
     * Metodo per generare un report HTML rapido (Logica Statica)
     */
    private String generaReportSistema(ComponenteDAO c, AllarmeDAO a, LogEnergiaDAO e) {
        int rotti = c.countRotti();
        int allarmi = a.countNonLetti();
        int batt = e.getUltimoLivelloBatteria();

        // Colori dinamici basati sullo stato
        String colorBatt = (batt < 30) ? "danger" : (batt < 60 ? "warning" : "success");
        String colorAllarmi = (allarmi > 0) ? "danger" : "success";
        String colorHardware = (rotti > 0) ? "warning" : "success";

        StringBuilder sb = new StringBuilder();
        sb.append("<div class='chatbot-report'>");
        sb.append("<p class='small text-muted mb-2 text-uppercase fw-bold'>Analisi Sistema Acquaponico</p>");
        
        // Riga Batteria
        sb.append("<div class='mb-2'>");
        sb.append("<span class='badge rounded-pill bg-").append(colorBatt).append("'>")
          .append("<i class='bi bi-lightning-charge-fill'></i> Batteria: ").append(batt).append("%</span> ");
        
        // Riga Allarmi
        sb.append("<span class='badge rounded-pill bg-").append(colorAllarmi).append("'>")
          .append("<i class='bi bi-exclamation-triangle-fill'></i> Allarmi: ").append(allarmi).append("</span> ");
        
        // Riga Hardware
        sb.append("<span class='badge rounded-pill bg-").append(colorHardware).append("'>")
          .append("<i class='bi bi-tools'></i> Guasti: ").append(rotti).append("</span>");
        sb.append("</div>");

        // Messaggio conclusivo rapido
        sb.append("<div class='p-2 border-start border-3 border-").append(colorAllarmi).append(" bg-light small'>");
        if (allarmi > 0 || rotti > 0) {
            sb.append("Richiesta attenzione immediata nei settori evidenziati.");
        } else {
            sb.append("Tutti i parametri sono nominali. Il sistema è in equilibrio.");
        }
        sb.append("</div></div>");

        return sb.toString();
    }

    /**
     * Chiamata alle API di Gemini
     */
    private String chiediAI(String message,String historyJson, String nome, ComponenteDAO c, AllarmeDAO a, LogEnergiaDAO e) {
    	 
        // Trasformiamo la history JSON in un formato leggibile per l'AI
        StringBuilder memoryBuilder = new StringBuilder();
        try {
            if (historyJson != null && !historyJson.equals("[]")) {
                org.json.JSONArray historyArray = new org.json.JSONArray(historyJson);
                for (int i = 0; i < historyArray.length(); i++) {
                    org.json.JSONObject msg = historyArray.getJSONObject(i);
                    String ruolo = msg.getString("autore").equals("user") ? "Utente" : "Assistente";
                    memoryBuilder.append(ruolo).append(": ").append(msg.getString("testo")).append("\n");
                }
            }
        } catch (Exception ex) {
            memoryBuilder.append("(Errore nel caricamento memoria precedente)\n");
        }
        // Prepariamo i dati reali per l'AI
        String contesto = String.format("Utente: %s. Batteria: %d%%. Allarmi non letti: %d. Componenti rotti: %d. CRONOLOGIA CONVERSAZIONE PRECEDENTE:%s", 
                          nome, e.getUltimoLivelloBatteria(), a.countNonLetti(), c.countRotti(), memoryBuilder.toString());
        
        // Costruzione del JSON
        String jsonBody = """
        	    {
        	      "system_instruction": {
        	        "parts": [{
        	          "text": "Sei l'assistente esperto del Sistema Acquaponico Smart. Dati attuali: %s. 
        	                   REGOLE DI FORMATTAZIONE HTML:
        	                   1. Usa <b>testo</b> per evidenziare termini tecnici o valori importanti.
        	                   2. Usa <br> per andare a capo e rendere il testo leggibile.
        	                   3. Usa <span class='badge bg-info text-dark'>termine</span> per etichette tecniche.
        	                   4. Se elenchi dei passaggi, usa i bullet point HTML (•).
        	                   5. Usa icone Bootstrap se necessario (es. <i class='bi bi-info-circle'></i>).
        	                   
        	                   Rispondi in modo professionale, amichevole e sintetico. 
        	                   Se la domanda è fuori contesto, rispondi con un messaggio di cortesia standard."
        	        }]
        	      },
        	      "contents": [{
        	        "parts": [{
        	          "text": "%s"
        	        }]
        	      }]
        	    }
        	    """.formatted(contesto.replace("\"", "'"), message.replace("\"", "'"));

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(GEMINI_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            
            if (response.statusCode() == 200) {
                return parseTextFromGemini(response.body());
            } else {
                return "L'AI è momentaneamente occupata, ma i tuoi dati sono al sicuro. Riprova tra poco!";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return "Errore di connessione al cervello AI.";
        }
    }

    private String parseTextFromGemini(String json) {
        try {
            org.json.JSONObject obj = new org.json.JSONObject(json);
            return obj.getJSONArray("candidates")
                      .getJSONObject(0)
                      .getJSONObject("content")
                      .getJSONArray("parts")
                      .getJSONObject(0)
                      .getString("text");
        } catch (Exception e) {
            return "Errore nell'interpretazione della risposta AI.";
        }
    }

    private boolean contieneParola(String messaggio, String[] paroleChiave) {
        for (String parola : paroleChiave) {
            if (messaggio.contains(parola)) return true;
        }
        return false;
    }
}