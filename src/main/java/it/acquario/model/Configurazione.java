package it.acquario.model;

import java.sql.Timestamp;

public class Configurazione {
    private int idConfig;
    private String parametro;
    private String valore;
    private TipoDato tipoDato; // Utilizziamo l'Enum invece della Stringa
    private String descrizione;
    private Timestamp ultimaModifica;
    private Categoria categoria;
    private double minimo;
    private double massimo;

    // Costruttore vuoto (necessario per i framework e JavaBean)
    public Configurazione() {}

    // Costruttore completo
    public Configurazione(int idConfig, String parametro, String valore, TipoDato tipoDato,Categoria categoria, String descrizione, Timestamp ultimaModifica , double minimo,  double massimo) {
        this.idConfig = idConfig;
        this.parametro = parametro;
        this.valore = valore;
        this.tipoDato = tipoDato;
        this.categoria=categoria;
        this.descrizione = descrizione;
        this.ultimaModifica = ultimaModifica;
        this.minimo = minimo;
        this.massimo = massimo;
    }

    // GETTER E SETTER
    public int getIdConfig() {
        return idConfig;
    }

    public void setIdConfig(int idConfig) {
        this.idConfig = idConfig;
    }

    public String getParametro() {
        return parametro;
    }

    public void setParametro(String parametro) {
        this.parametro = parametro;
    }

    public String getValore() {
        return valore;
    }

    public void setValore(String valore) {
        this.valore = valore;
    }

    public TipoDato getTipoDato() {
        return tipoDato;
    }

    public void setTipoDato(TipoDato tipoDato) {
        this.tipoDato = tipoDato;
    }
    
    public Categoria getCategoria() { 
    	return categoria; 
    }
    
    public void setCategoria(Categoria categoria) { 
    	this.categoria = categoria; 
    }
    
    public double getMinimo() { 
    	return minimo; 
    }

    public void setMinimo(double minimo) { 
    	this.minimo = minimo; 
    }
     
    public double getMassimo() { 
    	return massimo; 
    }
     
    public void setMassimo(double massimo) { 
    	this.massimo = massimo;
    }
    
    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public Timestamp getUltimaModifica() {
        return ultimaModifica;
    }

    public void setUltimaModifica(Timestamp ultimaModifica) {
        this.ultimaModifica = ultimaModifica;
    }
    
   
    
    
   


    // Metodo helper per ottenere il valore come Float (comodo per pH e Temp)
    public float getValoreAsFloat() {
        try {
            return Float.parseFloat(this.valore);
        } catch (Exception e) {
            return 0.0f;
        }
    }

    // Metodo helper per ottenere il valore come Integer (comodo per timer e cicli)
    public int getValoreAsInt() {
        try {
            return Integer.parseInt(this.valore);
        } catch (Exception e) {
            return 0;
        }
    }
}