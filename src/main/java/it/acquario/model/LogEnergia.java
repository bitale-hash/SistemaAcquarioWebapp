package it.acquario.model;

import java.sql.Timestamp;

public class LogEnergia {
    private int idLog;
    private Timestamp dataOra;
    private int livelloBatteria;
    private float tensioneVolt;
    private float correnteProdottaMa;
    private float correnteConsumataMa;
    private String erroreRilevato;

    // Costruttore vuoto
    public LogEnergia() {}

    // Getter e Setter
    public int getIdLog() { return idLog; }
    public void setIdLog(int idLog) { this.idLog = idLog; }

    public Timestamp getDataOra() { return dataOra; }
    public void setDataOra(Timestamp dataOra) { this.dataOra = dataOra; }

    public int getLivelloBatteria() { return livelloBatteria; }
    public void setLivelloBatteria(int livelloBatteria) { this.livelloBatteria = livelloBatteria; }

    public float getTensioneVolt() { return tensioneVolt; }
    public void setTensioneVolt(float tensioneVolt) { this.tensioneVolt = tensioneVolt; }

    public float getCorrenteProdottaMa() { return correnteProdottaMa; }
    public void setCorrenteProdottaMa(float correnteProdottaMa) { this.correnteProdottaMa = correnteProdottaMa; }

    public float getCorrenteConsumataMa() { return correnteConsumataMa; }
    public void setCorrenteConsumataMa(float correnteConsumataMa) { this.correnteConsumataMa = correnteConsumataMa; }

    public String getErroreRilevato() { return erroreRilevato; }
    public void setErroreRilevato(String erroreRilevato) { this.erroreRilevato = erroreRilevato; }
}