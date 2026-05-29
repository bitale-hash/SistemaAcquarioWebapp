package it.acquario.model;

import java.sql.Timestamp;

public class LogAmbientale {
    
    private int idLog;
    private Timestamp dataOra;
    
    // PARAMETRI ACQUARIO (Usiamo i Wrapper Object per gestire i valori NULL del DB)
    private Double temperaturaVasca;
    private Double phVasca;
    private Integer livelloAcquaVascaCm;
    
    // PARAMETRI PIANTE / SERRA
    private Integer acquaPianteMl;
    private Integer umiditaTerreno;
    private Double temperaturaAria;
    private Integer umiditaAria;
    private Integer luminositaLux;

    // Costruttore Vuoto (Standard JavaBean)
    public LogAmbientale() {
    }

    // Costruttore Completo (Utile per quando estrai i dati dal DAO)
    public LogAmbientale(int idLog, Timestamp dataOra, Double temperaturaVasca, Double phVasca, 
                         Integer livelloAcquaVascaCm, Integer acquaPianteMl, Integer umiditaTerreno, 
                         Double temperaturaAria, Integer umiditaAria, Integer luminositaLux) {
        this.idLog = idLog;
        this.dataOra = dataOra;
        this.temperaturaVasca = temperaturaVasca;
        this.phVasca = phVasca;
        this.livelloAcquaVascaCm = livelloAcquaVascaCm;
        this.acquaPianteMl = acquaPianteMl;
        this.umiditaTerreno = umiditaTerreno;
        this.temperaturaAria = temperaturaAria;
        this.umiditaAria = umiditaAria;
        this.luminositaLux = luminositaLux;
    }

    // --- GETTER E SETTER ---

    public int getIdLog() {
        return idLog;
    }

    public void setIdLog(int idLog) {
        this.idLog = idLog;
    }

    public Timestamp getDataOra() {
        return dataOra;
    }

    public void setDataOra(Timestamp dataOra) {
        this.dataOra = dataOra;
    }

    public Double getTemperaturaVasca() {
        return temperaturaVasca;
    }

    public void setTemperaturaVasca(Double temperaturaVasca) {
        this.temperaturaVasca = temperaturaVasca;
    }

    public Double getPhVasca() {
        return phVasca;
    }

    public void setPhVasca(Double phVasca) {
        this.phVasca = phVasca;
    }

    public Integer getLivelloAcquaVascaCm() {
        return livelloAcquaVascaCm;
    }

    public void setLivelloAcquaVascaCm(Integer livelloAcquaVascaCm) {
        this.livelloAcquaVascaCm = livelloAcquaVascaCm;
    }

    public Integer getAcquaPianteMl() {
        return acquaPianteMl;
    }

    public void setAcquaPianteMl(Integer acquaPianteMl) {
        this.acquaPianteMl = acquaPianteMl;
    }

    public Integer getUmiditaTerreno() {
        return umiditaTerreno;
    }

    public void setUmiditaTerreno(Integer umiditaTerreno) {
        this.umiditaTerreno = umiditaTerreno;
    }

    public Double getTemperaturaAria() {
        return temperaturaAria;
    }

    public void setTemperaturaAria(Double temperaturaAria) {
        this.temperaturaAria = temperaturaAria;
    }

    public Integer getUmiditaAria() {
        return umiditaAria;
    }

    public void setUmiditaAria(Integer umiditaAria) {
        this.umiditaAria = umiditaAria;
    }

    public Integer getLuminositaLux() {
        return luminositaLux;
    }

    public void setLuminositaLux(Integer luminositaLux) {
        this.luminositaLux = luminositaLux;
    }

    @Override
    public String toString() {
        return "LogAmbientale{" + "idLog=" + idLog + ", dataOra=" + dataOra + 
                ", tempVasca=" + temperaturaVasca + ", ph=" + phVasca + 
                ", acquaPiante=" + acquaPianteMl + ", umiditaTerreno=" + umiditaTerreno + '}';
    }
}