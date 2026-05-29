<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, it.acquario.model.LogEnergia" %>
<%
    List<LogEnergia> storico = (List<LogEnergia>) request.getAttribute("listaStorico");
    // Invertiamo la lista per il grafico (dal più vecchio al più nuovo)
    if(storico != null) Collections.reverse(storico);
%>
<!DOCTYPE html>
<html>
<head>
    <title>Storico Energia - SistemaAcquario</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css">
    <!-- Chart.js -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body class="bg-light">
	 
    <nav class="navbar navbar-dark bg-dark mb-4">
        <div class="container">
            <a class="navbar-brand" href="DashboardServlet"><i class="bi bi-arrow-left-circle me-2"></i> Torna alla Dashboard</a>
        </div>
    </nav>

    <div class="container mb-5">
        <h2 class="fw-bold mb-4">Analisi Energetica</h2>

        <!-- SEZIONE GRAFICO -->
        <div class="card shadow-sm mb-4">
            <div class="card-body">
                <canvas id="graficoEnergia" style="width: 100%; height: 350px;"></canvas>
            </div>
        </div>

        <!-- TABELLA DATI -->
        <div class="card shadow-sm">
            <div class="card-header bg-white fw-bold">Dettaglio Log (Ultimi 50)</div>
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead class="table-light">
                        <tr>
                            <th>Data/Ora</th>
                            <th>Batteria</th>
                            <th>Tensione</th>
                            <th>Produzione</th>
                            <th>Consumo</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if(storico != null) { 
                           for(LogEnergia l : storico) { %>
                            <tr>
                                <td class="small"><%= l.getDataOra() %></td>
                                <td><span class="badge <%= l.getLivelloBatteria() < 20 ? "bg-danger" : "bg-success" %>"><%= l.getLivelloBatteria() %>%</span></td>
                                <td><%= l.getTensioneVolt() %>V</td>
                                <td class="text-primary">+<%= l.getCorrenteProdottaMa() %> mA</td>
                                <td class="text-danger">-<%= l.getCorrenteConsumataMa() %> mA</td>
                            </tr>
                        <% } } %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <script>
    const ctx = document.getElementById('graficoEnergia').getContext('2d');
    new Chart(ctx, {
        type: 'line',
        data: {
            labels: [<% if(storico != null) { for(LogEnergia l : storico) { %> "<%= l.getDataOra().getHours() %>:<%= l.getDataOra().getMinutes() %>", <% } } %>],
            datasets: [{
                label: 'Livello Batteria %',
                data: [<% if(storico != null) { for(LogEnergia l : storico) { %> <%= l.getLivelloBatteria() %>, <% } } %>],
                borderColor: '#198754',
                backgroundColor: 'rgba(25, 135, 84, 0.1)',
                yAxisID: 'yBatteria', // Asse dedicato alla batteria
                fill: true,
                tension: 0.4
            },
            {
                label: 'Consumo (mA)',
                data: [<% if(storico != null) { for(LogEnergia l : storico) { %> <%= l.getCorrenteConsumataMa() %>, <% } } %>],
                borderColor: '#dc3545',
                yAxisID: 'yCorrente', // Asse dedicato alla corrente
                borderDash: [5, 5],
                fill: false,
                tension: 0.4
            }]
        },
        options: {
            responsive: true,
            scales: {
                yBatteria: {
                    type: 'linear',
                    display: true,
                    position: 'left',
                    min: 0,
                    max: 100, // Forza il massimo al 100%
                    title: { display: true, text: 'Batteria (%)' }
                },
                yCorrente: {
                    type: 'linear',
                    display: true,
                    position: 'right', // Mettiamo i mA a destra per non confonderli
                    grid: { drawOnChartArea: false }, // Evita troppe linee sovrapposte
                    title: { display: true, text: 'Corrente (mA)' }
                }
            }
        }
    });
    </script>
</body>
</html>