package it.acquario.model;

public class Pesce {
    private int idPesce;
    private String specie;
    private String tipoCibo;
    private double tempMin;
    private double tempMax;
    private float phMin;
    private float phMax;
    private int numMaxMc; // Aggiornato da MQ a MC
    private String segnaliStress;

    public Pesce() {}

    public Pesce(int idPesce, String specie, String tipoCibo, double tempMin, double tempMax, int numMaxMc, String segnaliStress) {
        this.idPesce = idPesce;
        this.specie = specie;
        this.tipoCibo = tipoCibo;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.numMaxMc = numMaxMc;
        this.segnaliStress = segnaliStress;
    }

    // Getter e Setter
    public int getIdPesce() { return idPesce; }
    public void setIdPesce(int idPesce) { this.idPesce = idPesce; }

    public String getSpecie() { return specie; }
    public void setSpecie(String specie) { this.specie = specie; }

    public String getTipoCibo() { return tipoCibo; }
    public void setTipoCibo(String tipoCibo) { this.tipoCibo = tipoCibo; }

    public double getTempMin() { return tempMin; }
    public void setTempMin(double tempMin) { this.tempMin = tempMin; }

    public double getTempMax() { return tempMax; }
    public void setTempMax(double tempMax) { this.tempMax = tempMax; }
    
    public float getPhMin() { return phMin; }
    public void setPhMin(float phMin) { this.phMin = phMin; }

    public float getPhMax() {  return phMax; }
    public void setPhMax(float phMax) {  this.phMax = phMax;  }

    public int getNumMaxMc() { return numMaxMc; }
    public void setNumMaxMc(int numMaxMc) { this.numMaxMc = numMaxMc; }

    public String getSegnaliStress() { return segnaliStress; }
    public void setSegnaliStress(String segnaliStress) { this.segnaliStress = segnaliStress; }
}