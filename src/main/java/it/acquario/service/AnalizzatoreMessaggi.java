package it.acquario.service;

import org.json.JSONObject;
import org.json.JSONException;
import java.sql.SQLException;

import it.acquario.model.StoricoMessaggio;
import it.acquario.model.LogAmbientale;
import it.acquario.model.LogEnergia;
import it.acquario.model.Configurazione;
import it.acquario.model.Allarme;

import it.acquario.dao.StoricoMessaggioDAO;
import it.acquario.dao.LogAmbientaleDAO;
import it.acquario.dao.LogEnergiaDAO;
import it.acquario.dao.ConfigurazioneDAO;
import it.acquario.dao.AllarmeDAO;

public class AnalizzatoreMessaggi {

    private StoricoMessaggioDAO storicoDao;
    private LogAmbientaleDAO logAmbientaleDao;
    private LogEnergiaDAO logEnergiaDao;
    private ConfigurazioneDAO configurazioneDao;
    private AllarmeDAO allarmeDao;

    public AnalizzatoreMessaggi() {
        this.storicoDao = new StoricoMessaggioDAO();
        this.logAmbientaleDao = new LogAmbientaleDAO();
        this.logEnergiaDao = new LogEnergiaDAO();
        this.configurazioneDao = new ConfigurazioneDAO();
        this.allarmeDao = new AllarmeDAO();
    }

    public void elaboraMessaggio(StoricoMessaggio msg) {
        String payloadRaw = msg.getMessaggio();
        System.out.println("[ANALIZZATORE] Avvio analisi sul messaggio ID: " + msg.getIdHistory());

        try {
            JSONObject json = new JSONObject(payloadRaw);
            boolean elaboratoConSuccesso = false;

            // =================================================================
            // 1. GESTIONE DATI AMBIENTALI & DETEZIONE ALLARMI SOGLIA
            // =================================================================
            LogAmbientale nuovoLogAmb = new LogAmbientale();
            boolean haDatiAmb = false;

            if (json.has("temp_vasca")) { nuovoLogAmb.setTemperaturaVasca(json.getDouble("temp_vasca")); haDatiAmb = true; }
            if (json.has("ph")) { nuovoLogAmb.setPhVasca(json.getDouble("ph")); haDatiAmb = true; }
            if (json.has("livello_acqua")) { nuovoLogAmb.setLivelloAcquaVascaCm(json.getInt("livello_acqua")); haDatiAmb = true; }
            if (json.has("acqua_piante")) { nuovoLogAmb.setAcquaPianteMl(json.getInt("acqua_piante")); haDatiAmb = true; }
            if (json.has("umidita_terreno")) { nuovoLogAmb.setUmiditaTerreno(json.getInt("umidita_terreno")); haDatiAmb = true; }
            if (json.has("temp_aria")) { nuovoLogAmb.setTemperaturaAria(json.getDouble("temp_aria")); haDatiAmb = true; }
            if (json.has("umidita_aria")) { nuovoLogAmb.setUmiditaAria(json.getInt("umidita_aria")); haDatiAmb = true; }
            if (json.has("luminosita")) { nuovoLogAmb.setLuminositaLux(json.getInt("luminosita")); haDatiAmb = true; }

            if (haDatiAmb) {
                // Salviamo le metriche ambientali nel DB
                logAmbientaleDao.inserisciLog(nuovoLogAmb);
                System.out.println("[ANALIZZATORE] Scritto record in log_ambientale.");
                elaboratoConSuccesso = true;

                // MOTORE ALLARMI: Confronto in tempo reale usando il tuo metodo 'getConfigByParametro'
                eseguiControlloSoglieBiologiche(nuovoLogAmb);
            }

            // =================================================================
            // 2. GESTIONE DATI ENERGETICI (LOG ENERGIA)
            // =================================================================
            LogEnergia nuovoLogEnergia = new LogEnergia();
            boolean haDatiEnergia = false;

            if (json.has("livello_batteria")) { nuovoLogEnergia.setLivelloBatteria(json.getInt("livello_batteria")); haDatiEnergia = true; }
            if (json.has("tensione_volt")) { nuovoLogEnergia.setTensioneVolt((float) json.getDouble("tensione_volt")); haDatiEnergia = true; }
            if (json.has("corrente_prodotta")) { nuovoLogEnergia.setCorrenteProdottaMa((float) json.getDouble("corrente_prodotta")); haDatiEnergia = true; }
            if (json.has("corrente_consumata")) { nuovoLogEnergia.setCorrenteConsumataMa((float) json.getDouble("corrente_consumata")); haDatiEnergia = true; }
            if (json.has("errore_rilevato")) { nuovoLogEnergia.setErroreRilevato(json.getString("errore_rilevato")); haDatiEnergia = true; }

            if (haDatiEnergia) {
                logEnergiaDao.inserisciLogEnergia(nuovoLogEnergia);
                System.out.println("[ANALIZZATORE] Scritto record in log_energia.");
                elaboratoConSuccesso = true;
            }

            // =================================================================
            // CHIUSURA LOGICA STATO MESSAGGIO RAW
            // =================================================================
            if (elaboratoConSuccesso) {
                storicoDao.marcaComeElaborato(msg.getIdHistory());
                System.out.println("[ANALIZZATORE] Messaggio ID " + msg.getIdHistory() + " contrassegnato come ESEGUITO.");
            } else {
                System.out.println("[ANALIZZATORE WARNING] Nessun campo mappabile trovato nell'ID " + msg.getIdHistory() + ". Chiudo la coda.");
                storicoDao.marcaComeElaborato(msg.getIdHistory());
            }

        } catch (JSONException e) {
            System.err.println("[ANALIZZATORE CRITICAL] Errore di formato JSON nell'ID: " + msg.getIdHistory());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("[ANALIZZATORE DATABASE ERROR] Errore di persistenza durante l'elaborazione dell'ID " + msg.getIdHistory());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("[ANALIZZATORE ERROR] Eccezione generica inattesa nell'ID " + msg.getIdHistory());
            e.printStackTrace();
        }
    }

    /**
     * Sotto-motore di controllo che estrae i limiti dal database tramite ConfigurazioneDAO 
     * e valuta se i parametri biologici dell'acquario scatenano un'anomalia.
     */
    /**
     * Sotto-motore di controllo che estrae i limiti dal database tramite ConfigurazioneDAO 
     * e valuta se i parametri biologici dell'acquario scatenano un'anomalia.
     */
    private void eseguiControlloSoglieBiologiche(LogAmbientale log) throws SQLException {
        
        // --- 1. CONTROLLO SOGLIA SUL pH ---
        if (log.getPhVasca() != null) {
            double phAttuale = log.getPhVasca();
            
            Configurazione confPhMin = configurazioneDao.getConfigByParametro("SOGLIA MINIMA PH ACQUA PER ALLARME");
            Configurazione confPhMax = configurazioneDao.getConfigByParametro("SOGLIA MASSIMA PH ACQUA PER ALLARME");

            if (confPhMin != null && confPhMin.getValore() != null) {
                double limiteMin = Double.parseDouble(confPhMin.getValore());
                if (phAttuale < limiteMin) {
                    generaAllarmeCritico(
                        "Livello pH inferiore alla soglia di sicurezza! Rilevato: " + phAttuale + " (Soglia min: " + limiteMin + ")",
                        it.acquario.model.LivelloAllarme.CRITICAL
                    );
                }
            }

            if (confPhMax != null && confPhMax.getValore() != null) {
                double limiteMax = Double.parseDouble(confPhMax.getValore());
                if (phAttuale > limiteMax) {
                    generaAllarmeCritico(
                        "Livello pH superiore alla soglia biologica! Rilevato: " + phAttuale + " (Soglia max: " + limiteMax + ")",
                        it.acquario.model.LivelloAllarme.CRITICAL
                    );
                }
            }
        }

        // --- 2. CONTROLLO SOGLIA SULLA TEMPERATURA ACQUA ---
        if (log.getTemperaturaVasca() != null) {
            double tempAttuale = log.getTemperaturaVasca();
            
            Configurazione confTempMin = configurazioneDao.getConfigByParametro("SOGLIA MINIMATEMPERATURA ACQUA (°C)");
            Configurazione confTempMax = configurazioneDao.getConfigByParametro("SOGLIA MASSIMA TEMPERATURA ACQUA (°C)");

            if (confTempMin != null && confTempMin.getValore() != null) {
                double limiteMin = Double.parseDouble(confTempMin.getValore());
                if (tempAttuale < limiteMin) {
                    generaAllarmeCritico(
                        "Temperatura acqua troppo fredda! Rilevato: " + tempAttuale + "°C (Min consentito: " + limiteMin + "°C)",
                        it.acquario.model.LivelloAllarme.WARNING
                    );
                }
            }

            if (confTempMax != null && confTempMax.getValore() != null) {
                double limiteMax = Double.parseDouble(confTempMax.getValore());
                if (tempAttuale > limiteMax) {
                    generaAllarmeCritico(
                        "Temperatura acqua in surriscaldamento! Rilevato: " + tempAttuale + "°C (Max consentito: " + limiteMax + "°C)",
                        it.acquario.model.LivelloAllarme.CRITICAL
                    );
                }
            }
        }
    }

    /**
     * Helper interno allineato al Modello Allarme inviato dall'utente
     */
    private void generaAllarmeCritico(String testoMessaggio, it.acquario.model.LivelloAllarme livello) {
        try {
            Allarme allarme = new Allarme();
            allarme.setMessaggio(testoMessaggio);
            allarme.setLivello(livello);
            allarme.setLetta(false);     // L'allarme è appena scattato, non è ancora letto
            allarme.setRisolto(false);    // L'allarme è attivo sul pannello
            
            // Inviamo l'oggetto mappato al tuo DAO reale
            allarmeDao.inserisciAllarme(allarme);
            System.err.println("[MOTORE ALLARMI] REGISTRATO NEL DB (" + livello + "): " + testoMessaggio);
        } catch (Exception e) {
            System.err.println("[MOTORE ALLARMI ERROR] Impossibile scrivere l'allarme tramite AllarmeDAO");
            e.printStackTrace();
        }
    }
}