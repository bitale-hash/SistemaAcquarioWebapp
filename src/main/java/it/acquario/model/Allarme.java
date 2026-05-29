package it.acquario.model;

import java.sql.Timestamp;

public class Allarme {
    private int id_allarme;
    private LivelloAllarme livello; 
    private String messaggio;
    private boolean letta;
    private Timestamp data_ora;
    private int id_componente_rif;
    private String nomeComponente;
    private boolean risolto;
    
    public Allarme() {}
 
    // Getter e Setter
    public int getId_allarme() { return id_allarme; }
    public void setId_allarme(int id_allarme) { this.id_allarme = id_allarme; }

    public LivelloAllarme getLivello() { return livello; }
    public void setLivello(LivelloAllarme livello) { this.livello = livello; }

    public String getMessaggio() { return messaggio; }
    public void setMessaggio(String messaggio) { this.messaggio = messaggio; }

    public boolean isLetta() { return letta; }
    public void setLetta(boolean letta) { this.letta = letta; }

    public Timestamp getData_ora() { return data_ora; }
    public void setData_ora(Timestamp data_ora) { this.data_ora = data_ora; }

    public int getId_componente_rif() { return id_componente_rif; }
    public void setId_componente_rif(int id_componente_rif) { this.id_componente_rif = id_componente_rif; }

    public String getNomeComponente() { return nomeComponente; }
    public void setNomeComponente(String nomeComponente) { this.nomeComponente = nomeComponente; }
    
    public boolean isRisolto() { return risolto; }
    public void setRisolto(boolean risolto) { this.risolto = risolto; }
    
  
    public String getClasseColore() {
        if (risolto) return "bg-success text-white"; // Verde se tutto OK
        if (livello == null) return "bg-secondary text-white";
        
        switch (livello) {
            case CRITICAL: return "bg-danger text-white";
            case WARNING:  return "bg-warning text-dark";
            case INFO:     return "bg-info text-white";
            default:       return "bg-secondary text-white";
        }
    }

}