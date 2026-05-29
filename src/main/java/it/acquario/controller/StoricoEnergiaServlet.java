package it.acquario.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import it.acquario.dao.LogEnergiaDAO;
import it.acquario.model.LogEnergia;

@WebServlet("/StoricoEnergiaServlet")
public class StoricoEnergiaServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LogEnergiaDAO dao = new LogEnergiaDAO();
        try {
            List<LogEnergia> storico = dao.getStoricoCompleto();
            request.setAttribute("listaStorico", storico);
            request.getRequestDispatcher("storico_energia.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("DashboardServlet");
        }
    }
}