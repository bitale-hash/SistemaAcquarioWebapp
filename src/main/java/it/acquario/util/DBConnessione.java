package it.acquario.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnessione {
    // Modifica 'nome_tuo_db' con il nome reale del tuo schema su MySQL Workbench
    private static final String URL = "jdbc:mysql://localhost:3306/db_acquario?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "1234"; // <--- Metti la tua password

    public static Connection getConnessione() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver caricato con successo!");
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL non trovato nel progetto!", e);
        }
    }
    
 // AGGIUNGI QUESTO PER TESTARE SUBITO:
    public static void main(String[] args) {
        try {
            Connection conn = getConnessione();
            if (conn != null) {
                System.out.println("--- CONNESSIONE RIUSCITA! ---");
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("--- CONNESSIONE FALLITA! ---");
            e.printStackTrace();
        }
    }
}