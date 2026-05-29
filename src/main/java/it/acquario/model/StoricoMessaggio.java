package it.acquario.model;

import java.sql.Timestamp;

public class StoricoMessaggio {
    private int idHistory;
    private String tipoControllo; // Corrisponde all'ENUM (FREQUENTE, GIORNALIERO, etc.)
    private String esito;         // Corrisponde all'ENUM (OK, ALLARME, INFO)
    private String origine; // Corrisponde a enum('SISTEMA', 'UTENTE')
    private String messaggio;     // Qui salverai il JSON o il testo
    private Timestamp timestamp;
    private boolean elaborato;
   

     
    
    
    public StoricoMessaggio() {}

    // Getter e Setter
    public int getIdHistory() { return idHistory; }
    public void setIdHistory(int idHistory) { this.idHistory = idHistory; }

    public String getTipoControllo() { return tipoControllo; }
    public void setTipoControllo(String tipoControllo) { this.tipoControllo = tipoControllo; }

    public String getEsito() { return esito; }
    public void setEsito(String esito) { this.esito = esito; }
    
    public String getOrigine() { return origine; }
    public void setOrigine(String origine) { this.origine = origine; }

    public String getMessaggio() { return messaggio; }
    public void setMessaggio(String messaggio) { this.messaggio = messaggio; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
    
    public boolean isElaborato() { return elaborato; }
    public void setElaborato(boolean elaborato) { this.elaborato = elaborato; }
    
}