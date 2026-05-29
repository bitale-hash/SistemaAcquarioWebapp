package it.acquario.controller;
 
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors; // Aggiunto per filtrare la lista facilmente

import it.acquario.dao.ConfigurazioneDAO;
import it.acquario.dao.LogAmbientaleDAO;
import it.acquario.dao.LogEnergiaDAO;
import it.acquario.model.Configurazione;
import it.acquario.model.DatiGraficoSerra;
import it.acquario.model.LogAmbientale;
import it.acquario.model.LogEnergia;
import it.acquario.dao.ComponenteDAO;
import it.acquario.dao.PesceDAO;
import it.acquario.dao.AllarmeDAO;
import it.acquario.model.Componente;
import it.acquario.model.Pesce;
import it.acquario.util.DBConnessione;
import it.acquario.model.Allarme;

@WebServlet("/DashboardServlet")
public class DashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("utente") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        ComponenteDAO compDao = new ComponenteDAO();
        AllarmeDAO alarmDao = new AllarmeDAO();
        ConfigurazioneDAO configDao = new ConfigurazioneDAO();
        PesceDAO pesceDao = new PesceDAO(); 
        LogEnergiaDAO energiaDAO = new LogEnergiaDAO();
        LogAmbientaleDAO logAmbientaleDAO = new LogAmbientaleDAO();
        // Aggiunto l'inizializzazione del DAO per lo storico messaggi
        it.acquario.dao.StoricoMessaggioDAO storicoMessaggioDAO = new it.acquario.dao.StoricoMessaggioDAO();
      
        try {
            // 1. RECUPERO COMPONENTI E FILTRO CRITICI (entro 30gg o Fine Vita)
            List<Componente> critici = compDao.getComponentiCritici(); 
            request.setAttribute("listaComponenti", critici);
             
            // 2. ENERGIA
            LogEnergia ultimoLog = energiaDAO.getUltimoStato();
            request.setAttribute("statoEnergia", ultimoLog);
            
            // 3. PESCI
            List<Pesce> listaPesci = pesceDao.getAllPesci();
            request.setAttribute("listaPesci", listaPesci);
            
            // ==========================================
            // GRAFICI
            // ==========================================
            List<LogAmbientale> listaAcquario = logAmbientaleDAO.getListaGraficoAcquario(); 
            List<DatiGraficoSerra> listaSerraCombined = logAmbientaleDAO.getListaGraficoSerra(); 
            
            // Formattatore per mostrare solo "Giorno Mese Ora:Minuti" sull'asse X (es. "20 Mag 15:30")
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMM HH:mm");

            // Preparazione stringhe per GRAFICO 1 (Salute Acquario)
            List<String> labelsAcquario = new ArrayList<>();
            List<String> valoriTemp = new ArrayList<>();
            List<String> valoriPh = new ArrayList<>();

            for (LogAmbientale log : listaAcquario) {
                labelsAcquario.add("'" + sdf.format(log.getDataOra()) + "'");
                valoriTemp.add(log.getTemperaturaVasca() != null ? String.valueOf(log.getTemperaturaVasca()) : "null");
                valoriPh.add(log.getPhVasca() != null ? String.valueOf(log.getPhVasca()) : "null");
            }

            // Preparazione stringhe per GRAFICO 2 (Serra: Luce & Idratazione unificati)
            List<String> labelsSerra = new ArrayList<>();
            List<String> valoriLuce = new ArrayList<>();
            List<String> valoriAcqua = new ArrayList<>();
            
            for (DatiGraficoSerra record : listaSerraCombined) {
                labelsSerra.add("'" + sdf.format(record.getDataOra()) + "'");
                valoriLuce.add(String.valueOf(record.getCorrenteProdottaMa()));
                valoriAcqua.add(String.valueOf(record.getAcquaPianteMl())); 
            }

            // Spedizione dei dati alla JSP sotto forma di Array JavaScript pronti all'uso
            request.setAttribute("labelsAcquario", labelsAcquario.toString());
            request.setAttribute("valoriTemp", valoriTemp.toString());
            request.setAttribute("valoriPh", valoriPh.toString());

            request.setAttribute("labelsLuce", labelsSerra.toString());
            request.setAttribute("valoriLuce", valoriLuce.toString());
            request.setAttribute("labelsAcqua", labelsSerra.toString()); 
            request.setAttribute("valoriAcqua", valoriAcqua.toString());
            // ==========================================
            
            // 4. CONFIGURAZIONI 
            
            	//Soglie biologiche automatiche dei pesci
	            Pesce soglieBio = pesceDao.getSoglieBiologicheOttimali();
	
	            // Passiamo i valori calcolati alla JSP
	            request.setAttribute("bioTempMin", soglieBio.getTempMin());
	            request.setAttribute("bioTempMax", soglieBio.getTempMax());
	            request.setAttribute("bioPhMin", soglieBio.getPhMin());
	            request.setAttribute("bioPhMax", soglieBio.getPhMax());
	            
	            
	        List<Configurazione> listaConfig = configDao.getAllConfigurazioni(); 
	        request.setAttribute("listaConfigurazioni", listaConfig);
            
            // 5. ALLARMI
            List<Allarme> listaTreAllarmi = alarmDao.getUltimiTreAllarmi();
            request.setAttribute("listaAllarmi", listaTreAllarmi);
            
            // 6. RECUPERO STORICO MESSAGGI (Aggiunto Ora per la Tab Diagnostica)
            List<it.acquario.model.StoricoMessaggio> listaMessaggi = storicoMessaggioDAO.getUltimiMessaggi(10);
            request.setAttribute("listaMessaggi", listaMessaggi);

        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            request.setAttribute("errore", "Errore nel database: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errore", "Errore generico di sistema.");
        }
        
        request.getRequestDispatcher("dashboard.jsp").forward(request, response);
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}