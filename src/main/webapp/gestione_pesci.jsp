<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, it.acquario.model.Pesce" %>
<%
    // Recuperiamo l'eventuale pesce da modificare inviato dalla Servlet
    Pesce edit = (Pesce) request.getAttribute("pesceDaModificare");
    
    // Logica per popolare il form: se 'edit' non è null, usiamo i suoi dati, altrimenti campi vuoti
    int idVal = (edit != null) ? edit.getIdPesce() : 0;
    String specieVal = (edit != null) ? edit.getSpecie() : "";
    String ciboVal = (edit != null) ? edit.getTipoCibo() : "";
    double tMinVal = (edit != null) ? edit.getTempMin() : 0.0;
    double tMaxVal = (edit != null) ? edit.getTempMax() : 0.0;
    float phMinVal = (edit != null) ? edit.getPhMin() : 0.0f; // <-- AGGIUNTO
    float phMaxVal = (edit != null) ? edit.getPhMax() : 0.0f; // <-- AGGIUNTO
    int mcVal = (edit != null) ? edit.getNumMaxMc() : 0;
    String stressVal = (edit != null) ? edit.getSegnaliStress() : "";
    
    String titoloCard = (edit != null) ? "Modifica Specie: " + specieVal : "Aggiungi Nuova Specie";
    String btnClass = (edit != null) ? "btn-warning" : "btn-primary";
    String btnTesto = (edit != null) ? "AGGIORNA SPECIE" : "SALVA SPECIE";
%>
<!DOCTYPE html>
<html>
<head>
    <title>Gestione Specie - Acquario</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css">
</head>
<body class="bg-light">
    <%@ include file="messaggi.jsp" %>

    <nav class="navbar navbar-dark bg-info mb-4 shadow">
        <div class="container">
            <a class="navbar-brand fw-bold" href="DashboardServlet"><i class="bi bi-arrow-left"></i> Dashboard</a>
            <span class="navbar-text text-white">Anagrafe Biologica</span>
        </div>
    </nav>

    <div class="container mb-5">
        <div class="card shadow-sm border-0 mb-4">
            <div class="card-header bg-white py-3 d-flex justify-content-between align-items-center">
                <h5 class="mb-0 fw-bold text-primary"><%= titoloCard %></h5>
                <% if(edit != null) { %>
                    <a href="GestionePesciServlet" class="btn btn-sm btn-outline-secondary">Annulla Modifica</a>
                <% } %>
            </div>
            <div class="card-body">
                <form action="GestionePesciServlet" method="POST" class="row g-3">
                	<input type="hidden" name="idPesce" value="<%= idVal %>">
                    
                    <div class="col-md-4">
                        <label class="form-label small fw-bold">Specie</label>
                        <input type="text" name="specie" class="form-control" value="<%= specieVal %>" required>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label small fw-bold">Alimentazione</label>
                        <input type="text" name="tipoCibo" class="form-control" value="<%= ciboVal %>">
                    </div>
                   <div class="col-md-2">
					    <label class="form-label small fw-bold">Temp Min (°C)</label>
					    <input type="number" step="0.1" id="tempMin" name="tempMin" class="form-control" value="<%= tMinVal %>" min="6" max="26" required>
					</div>
					<div class="col-md-2">
					    <label class="form-label small fw-bold">Temp Max (°C)</label>
					    <input type="number" step="0.1" id="tempMax" name="tempMax" class="form-control" value="<%= tMaxVal %>" min="6" max="26" required>
					</div>
					<div class="col-md-2">
					    <label class="form-label small fw-bold">pH Min</label>
					    <input type="number" step="0.1" id="phMin" name="phMin" class="form-control" value="<%= phMinVal %>" min="6" max="9" required>
					</div>
					<div class="col-md-2">
					    <label class="form-label small fw-bold">pH Max</label>
					    <input type="number" step="0.1" id="phMax" name="phMax" class="form-control" value="<%= phMaxVal %>" min="6" max="9" required>
					</div>

                    <div class="col-md-2">
                        <label class="form-label small fw-bold">Densità Max (pesci/m³)</label>
                        <input type="number" name="numMaxMc" class="form-control" value="<%= mcVal %>" min="1" max="1000">
                    </div>
                    <div class="col-md-3">
                        <label class="form-label small fw-bold">Segnali di Stress</label>
                        <input type="text" name="segnaliStress" class="form-control" value="<%= stressVal %>">
                    </div>
                    <div class="col-md-3 d-flex align-items-end">
                        <button type="submit" class="btn <%= btnClass %> w-100 shadow-sm fw-bold">
                            <i class="bi bi-check-lg"></i> <%= btnTesto %>
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <div class="card shadow-sm border-0">
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead class="table-dark">
                        <tr>
                            <th>Specie</th>
                            <th>Cibo</th>
                            <th>Range Temp.</th>
                            <th>Range pH</th> <%-- AGGIUNTO COLOONNA --%>
                            <th>Densità</th>
                            <th class="text-center">Azioni</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% 
                        List<Pesce> lista = (List<Pesce>) request.getAttribute("listaPesci");
                        if(lista != null && !lista.isEmpty()) {
                            for(Pesce p : lista) { 
                        %>
                            <tr>
                                <td class="fw-bold"><%= p.getSpecie() %></td>
                                <td><span class="badge bg-light text-dark border"><%= p.getTipoCibo() %></span></td>
                                <td><i class="bi bi-thermometer-half text-danger"></i> <%= p.getTempMin() %> - <%= p.getTempMax() %>°C</td>
                                <td><i class="bi bi-droplet-half text-primary"></i> <%= p.getPhMin() %> - <%= p.getPhMax() %></td> <%-- STAMPA pH --%>
                                <td><%= p.getNumMaxMc() %>/m³</td>
                                <td class="text-center">
                                    <a href="GestionePesciServlet?azione=preparaModifica&id=<%= p.getIdPesce() %>" 
                                       class="btn btn-sm btn-outline-primary me-1" title="Modifica">
                                        <i class="bi bi-pencil"></i>
                                    </a>
                                    <a href="GestionePesciServlet?azione=elimina&id=<%= p.getIdPesce() %>" 
                                       class="btn btn-sm btn-outline-danger" 
                                       onclick="return confirm('Vuoi davvero rimuovere questa specie?')" title="Elimina">
                                        <i class="bi bi-trash"></i>
                                    </a>
                                </td>
                            </tr>
                        <% 
                            } 
                        } else { 
                        %>
                            <tr><td colspan="6" class="text-center py-4 text-muted">Nessuna specie registrata.</td></tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
    
<script>
    function allineaLimitiIncrociati() {
        const tMin = document.getElementById('tempMin');
        const tMax = document.getElementById('tempMax');
        const pMin = document.getElementById('phMin');
        const pMax = document.getElementById('phMax');

        // Allineamento logico della Temperatura
        // Il minimo non può superare il valore attuale del massimo, e viceversa
        if (tMin.value) tMax.min = tMin.value;
        if (tMax.value) tMin.max = tMax.value-8;

        // Allineamento logico del pH
        if (pMin.value) pMax.min = pMin.value;
        if (pMax.value) pMin.max = pMax.value-1.4;
    }

    // Eseguiamo la funzione SUBITO al caricamento della pagina (Risolve il problema della modifica!)
    document.addEventListener("DOMContentLoaded", function() {
        allineaLimitiIncrociati();

        // Colleghiamo la funzione agli eventi per intercettare i cambi manuali futuri
        document.getElementById('tempMin').addEventListener('input', allineaLimitiIncrociati);
        document.getElementById('tempMax').addEventListener('input', allineaLimitiIncrociati);
        document.getElementById('phMin').addEventListener('input', allineaLimitiIncrociati);
        document.getElementById('phMax').addEventListener('input', allineaLimitiIncrociati);
    });
</script>
    
    <script>
        document.addEventListener("DOMContentLoaded", function() {
            // Se siamo in modalità modifica, puliamo l'URL in modo che il tasto "Indietro" funzioni bene
            <% if (edit != null) { %>
                if (window.history.replaceState) {
                    window.history.replaceState(null, null, window.location.pathname);
                }
            <% } %>
        });

        // SCRIPT ANTI-CACHE
        window.addEventListener('pageshow', function(event) {
            if (event.persisted || (window.performance && window.performance.navigation.type === 2)) {
                window.location.replace(window.location.href);
            }
        });

        // Auto-chiusura alert dopo 3 secondi
        setTimeout(function() {
            const alerts = document.querySelectorAll('.alert');
            alerts.forEach(a => {
                const bsAlert = new bootstrap.Alert(a);
                bsAlert.close();
            });
        }, 3000);
    </script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>