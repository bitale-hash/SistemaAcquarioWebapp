package it.acquario.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.sql.Date;

public class Componente {
    private int id_componente;
    private String nome;
    private BigDecimal prezzo;
    private Date data_acquisto;
    private int manutenzione_ogni_giorni;
    private int vita_media_mesi;
    private String foto_scontrino_path;
    private String note;
    private Date ultima_manutenzione;
    boolean rotto;
    boolean difettoso;

    // Costruttore vuoto
    public Componente() {}

    

    // Getter e Setter
    public int getId_componente() { return id_componente; }
    public void setId_componente(int id_componente) { this.id_componente = id_componente; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public BigDecimal getPrezzo() { return prezzo; }
    public void setPrezzo(BigDecimal prezzo) { this.prezzo = prezzo; }

    public Date getData_acquisto() { return data_acquisto; }
    public void setData_acquisto(Date data_acquisto) { this.data_acquisto = data_acquisto; }

    public int getManutenzione_ogni_giorni() { return manutenzione_ogni_giorni; }
    public void setManutenzione_ogni_giorni(int manutenzione_ogni_giorni) { this.manutenzione_ogni_giorni = manutenzione_ogni_giorni; }

    public int getVita_media_mesi() { return vita_media_mesi; }
    public void setVita_media_mesi(int vita_media_mesi) { this.vita_media_mesi = vita_media_mesi; }

    public String getFoto_scontrino_path() { return foto_scontrino_path; }
    public void setFoto_scontrino_path(String foto_scontrino_path) { this.foto_scontrino_path = foto_scontrino_path; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    
    public Date getUltima_manutenzione() { return ultima_manutenzione; }
    public void setUltima_manutenzione(Date ultima_manutenzione) { this.ultima_manutenzione = ultima_manutenzione; }
    
    public boolean isRotto() { return rotto; }
    public void setRotto(boolean rotto) { this.rotto = rotto; }
    
    public boolean isDifettoso() { return difettoso; }
    public void setDifettoso(boolean difettoso) { this.difettoso = difettoso; }

    //Other methods
    
 // 1. Logica Manutenzione (Ciclica)
    public int getGiorniAllaManutenzione() {
        // Se ultima_manutenzione è NULL, usa data_acquisto, altrimenti usa ultima_manutenzione
        LocalDate dataBase = (ultima_manutenzione != null) ? 
                             ultima_manutenzione.toLocalDate() : 
                             data_acquisto.toLocalDate();
                             
        LocalDate scadenza = dataBase.plusDays(manutenzione_ogni_giorni);
        return (int) ChronoUnit.DAYS.between(LocalDate.now(), scadenza);
    }
    
    
    // 2. Logica Vita Media (Fine vita componente)
    public boolean isFineVita() {
        if ( vita_media_mesi == 0) return false;
        LocalDate scadenzaVita = data_acquisto.toLocalDate().plusMonths(vita_media_mesi);
        return LocalDate.now().isAfter(scadenzaVita);
    }



}