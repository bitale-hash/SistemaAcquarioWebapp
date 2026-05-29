package it.acquario.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import it.acquario.dao.UtenteDAO;
import it.acquario.model.Utente;

// Questa annotazione collega la JSP a questa classe
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String user = request.getParameter("username");
        String pass = request.getParameter("password");

        // 1. Chiamiamo il DAO (che scriveremo tra poco)
        UtenteDAO dao = new UtenteDAO();
        Utente utenteLoggato = dao.validaLogin(user, pass);

        if (utenteLoggato != null) {
            // 2. Salviamo l'utente in sessione per ricordarcelo nelle altre pagine
            request.getSession().setAttribute("utente", utenteLoggato);
            
            // 3. Reindirizziamo alla DashboardServlet
            response.sendRedirect("DashboardServlet");
        } else {
            // 4. Se il DAO restituisce null, le credenziali nel DB non esistono
            request.setAttribute("errore", "Username o Password errati nel Database!");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}