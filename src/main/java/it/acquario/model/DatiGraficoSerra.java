package it.acquario.model;

import java.sql.Timestamp;

public class DatiGraficoSerra {
    private Timestamp dataOra;
    private Integer acquaPianteMl;
    private Double correnteProdottaMa; // Preso da log_energia

    public DatiGraficoSerra(Timestamp dataOra, Integer acquaPianteMl, Double correnteProdottaMa) {
        this.dataOra = dataOra;
        this.acquaPianteMl = acquaPianteMl;
        this.correnteProdottaMa = correnteProdottaMa;
    }

    public Timestamp getDataOra() { return dataOra; }
    public Integer getAcquaPianteMl() { return acquaPianteMl; }
    public Double getCorrenteProdottaMa() { return correnteProdottaMa; }
}