package it.acquario.model;

public class Utente {
    private int id_utente;
    private String username;
    private String password;
    private String ruolo;
    private String email;

    // Costruttore vuoto (serve per le JSP/Servlet)
    public Utente() {}

    // Getter e Setter
    public int getId() { return id_utente; }
    public void setId(int id_utente) { this.id_utente = id_utente; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRuolo() { return ruolo; }
    public void setRuolo(String ruolo) { this.ruolo = ruolo; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}