

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="it.acquario.model.*" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<%
    Utente utente = (Utente) session.getAttribute("utente");
    List<Componente> listaComponenti = (List<Componente>) request.getAttribute("listaComponenti");
    List<Allarme> listaAllarmi = (List<Allarme>) request.getAttribute("listaAllarmi");
    List<Pesce> listaPesci = (List<Pesce>) request.getAttribute("listaPesci");
    List<Configurazione> listaC = (List<Configurazione>) request.getAttribute("listaConfigurazioni");
    
    String username = (utente != null) ? utente.getUsername() : "Ospite";
%>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard Acquario</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <!-- CSS Separato -->
    <link rel="stylesheet" href="css/dashboard.css">
</head>
<body class="bg-light">
    <%@ include file="messaggi.jsp" %>

    <nav class="navbar navbar-expand-lg navbar-dark bg-dark mb-4 shadow">
        <div class="container">
            <a class="navbar-brand fw-bold" href="#"><i class="bi bi-droplet-half text-info"></i> SistemaAcquario</a>
            <div class="navbar-text text-white">
                <i class="bi bi-person-circle"></i> <%= username %> | 
                <a href="LogoutServlet" class="btn btn-outline-danger btn-sm ms-2">Esci</a>
            </div>
        </div>
    </nav>
    

   <%-- MENU DI NAVIGAZIONE A SCHEDE (TABS) PER ACCORCIARE LA PAGINA --%>
<ul class="nav nav-pills nav-justified mb-4 shadow-sm p-1 bg-white rounded-pill" id="dashboardTabs" role="tablist">
    <li class="nav-item" role="presentation">
        <button class="nav-link active rounded-pill fw-bold" id="telemetria-tab" data-bs-toggle="tab" data-bs-target="#telemetria" type="button" role="tab" aria-controls="telemetria" aria-selected="true">
            📊 Monitoraggio & Grafici
        </button>
    </li>
    <li class="nav-item" role="presentation">
        <button class="nav-link rounded-pill fw-bold" id="ecosistema-tab" data-bs-toggle="tab" data-bs-target="#ecosistema" type="button" role="tab" aria-controls="ecosistema" aria-selected="false">
            🌿 Componenti & Ecosistema
        </button>
    </li>
    <li class="nav-item" role="presentation">
        <button class="nav-link rounded-pill fw-bold" id="hardware-tab" data-bs-toggle="tab" data-bs-target="#hardware" type="button" role="tab" aria-controls="hardware" aria-selected="false">
            ⚙️ Pannello ESP32
        </button>
    </li>
    <li class="nav-item" role="presentation">
        <button class="nav-link rounded-pill fw-bold" id="diagnostica-tab" data-bs-toggle="tab" data-bs-target="#diagnostica" type="button" role="tab" aria-controls="diagnostica" aria-selected="false">
            💾 Messaggi RAW
        </button>
    </li>
</ul>

<%-- CONTENITORE PRINCIPALE DELLE SCHEDE --%>
<div class="tab-content" id="dashboardTabsContent">

    <%-- ========================================================================= --%>
    <%-- 1) TAB: MONITORAGGIO & GRAFICI                                            --%>
    <%-- ========================================================================= --%>
    <div class="tab-pane fade show active" id="telemetria" role="tabpanel" aria-labelledby="telemetria-tab">
        <div class="row g-4">
            <%-- SOTTO-COLONNA SINISTRA: HARDWARE (ENERGIA & ALLARMI) --%>
            <div class="col-lg-6">
                <%-- ENERGIA --%>
                <div class="card shadow-sm mb-4">
                    <div class="card-header bg-warning text-dark py-3">
                        <h5 class="mb-0 fw-bold"><i class="bi bi-lightning-charge-fill me-2"></i>Stato Energia</h5>
                    </div>
                    <div class="card-body">
                        <% it.acquario.model.LogEnergia energia = (it.acquario.model.LogEnergia) request.getAttribute("statoEnergia"); 
                           if (energia != null) { %>
                            <div class="text-center">
                                <div class="h1 fw-bold <%= energia.getLivelloBatteria() < 20 ? "text-danger" : "text-success" %>"><%= energia.getLivelloBatteria() %>%</div>
                                <div class="progress" style="height: 10px;">
                                    <div class="progress-bar <%= energia.getLivelloBatteria() < 20 ? "bg-danger" : "bg-success" %>" style="width: <%= energia.getLivelloBatteria() %>%"></div>
                                </div>
                            </div>
                        <% } else { %>
                            <div class="text-center text-muted py-2">Nessun dato energetico disponibile.</div>
                        <% } %>
                    </div>
                    <div class="card-footer text-center bg-white border-top-0">
                        <a href="StoricoEnergiaServlet" class="btn btn-link btn-sm text-decoration-none fw-bold">
                            Mostra storico completo <i class="bi bi-arrow-right-short"></i>
                        </a>
                    </div>
                </div>
                
                <%-- ALLARMI --%>
                <div class="card shadow-sm">
                    <div class="card-header bg-danger text-white py-3">
                        <h5 class="mb-0 fw-bold"><i class="bi bi-bell-fill me-2"></i>Ultimi Allarmi</h5>
                    </div>
                    <div class="list-group list-group-flush">
                        <% 
                        if (listaAllarmi != null && !listaAllarmi.isEmpty()) {
                            for (Allarme a : listaAllarmi) { 
                                String color = "info"; 
                                if (a.isRisolto()) {
                                    color = "success";
                                } else if (a.getLivello() != null) {
                                    switch (a.getLivello()) {
                                        case CRITICAL: color = "danger";  break;
                                        case WARNING:  color = "warning"; break;
                                        case INFO:     color = "info";    break;
                                    }
                                }
                                String opacita = (a.isLetta()) ? "opacity-50" : "";
                        %>
                            <div class="list-group-item border-start border-4 border-<%= color %> py-3 <%= opacita %>">
                                <div class="d-flex align-items-center justify-content-between">
                                    <div class="flex-grow-1 me-2">
                                        <small class="text-uppercase fw-bold text-<%= color %>" style="font-size: 0.7rem;">
                                            <%= a.isRisolto() ? "RISOLTO" : a.getLivello() %>
                                        </small>
                                        <p class="mb-0 small fw-medium text-dark"><%= a.getMessaggio() %></p>
                                    </div>
                                    <div class="d-flex gap-1">
                                        <% if(!a.isLetta()) { %>
                                            <form action="StoricoAllarmiServlet" method="POST" class="m-0">
                                                <input type="hidden" name="azione" value="leggiSingolo">
                                                <input type="hidden" name="id" value="<%= a.getId_allarme() %>">
                                                <input type="hidden" name="from" value="dashboard">
                                                <button type="submit" class="btn btn-sm btn-light rounded-circle border" title="Segna come letto">
                                                    <i class="bi bi-check-lg"></i>
                                                </button>
                                            </form>
                                        <% } %>
                                        <% if(!a.isRisolto() && a.getLivello() != it.acquario.model.LivelloAllarme.INFO) { %>
                                            <form action="StoricoAllarmiServlet" method="POST" class="m-0">
                                                <input type="hidden" name="azione" value="risolviSingolo">
                                                <input type="hidden" name="id" value="<%= a.getId_allarme() %>">
                                                <input type="hidden" name="from" value="dashboard">
                                                <button type="submit" class="btn btn-sm btn-success rounded-circle" title="Risolvi Problema">
                                                    <i class="bi bi-wrench"></i>
                                                </button>
                                            </form>
                                        <% } %>
                                    </div>
                                </div>
                            </div> 
                        <% 
                            } 
                        } else { 
                        %>
                            <div class="list-group-item py-4 text-center text-muted fst-italic small">
                                <i class="bi bi-shield-check text-success me-1"></i> Nessun allarme attivo nell'acquario.
                            </div>
                        <% } %>
                    </div> 
                    <div class="card-footer text-center bg-white border-top-0">
                        <a href="StoricoAllarmiServlet" class="btn btn-link btn-sm text-decoration-none fw-bold">
                            Mostra storico completo <i class="bi bi-arrow-right-short"></i>
                        </a>
                    </div>
                </div>
            </div>

          <%-- SOTTO-COLONNA DESTRA: GRAFICI TEMPO REALE --%>
			<div class="col-lg-6">
			    <%-- GRAFICO TEMPERATURA & PH --%>
			    <div class="card shadow-sm mb-4">
			        <div class="card-header bg-primary text-white py-3">
			            <h5 class="mb-0 fw-bold"><i class="bi bi-graph-up me-2"></i>Clima & Chimica dell'Acqua</h5>
			        </div>
			        <div class="card-body" style="position: relative; height: 300px;">
			            <canvas id="chartSaluteAcquario"></canvas>
			        </div>
			    </div>
			
			    <%-- GRAFICO ENERGIA PRODOTTA & ACQUA CONSUMATA --%>
			    <div class="card shadow-sm">
			        <div class="card-header bg-success text-white py-3">
			            <h5 class="mb-0 fw-bold"><i class="bi bi-bar-chart-line me-2"></i>Monitoraggio Luce & Idratazione</h5>
			        </div>
			        <div class="card-body" style="position: relative; height: 300px;">
			            <canvas id="chartLuceIdratazione"></canvas>
			        </div>
			    </div>
			</div>
			
			<%-- ======================================================== --%>
			<%-- SCRIPT JAVASCRIPT PER RENDERING DEI GRAFICI CON CHART.JS --%>
			<%-- ======================================================== --%>
			<%-- Inclusione di Chart.js e del tuo file personalizzato appena creato --%>
				<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
				<script src="${pageContext.request.contextPath}/js/graficiDashboard.js"></script>
				
				<script>
				document.addEventListener("DOMContentLoaded", function() {
				    // Configura l'oggetto dati prendendolo in modo sicuro dal server Java
				    var datiConfigGrafici = {
				        labelsAcquario: <%= request.getAttribute("labelsAcquario") != null ? request.getAttribute("labelsAcquario") : "[]" %>,
				        datiTemp: <%= request.getAttribute("valoriTemp") != null ? request.getAttribute("valoriTemp") : "[]" %>,
				        datiPh: <%= request.getAttribute("valoriPh") != null ? request.getAttribute("valoriPh") : "[]" %>,
				        labelsSerra: <%= request.getAttribute("labelsLuce") != null ? request.getAttribute("labelsLuce") : "[]" %>,
				        datiLuce: <%= request.getAttribute("valoriLuce") != null ? request.getAttribute("valoriLuce") : "[]" %>,
				        datiAcqua: <%= request.getAttribute("valoriAcqua") != null ? request.getAttribute("valoriAcqua") : "[]" %>
				    };
				
				    // Richiama la funzione definita nel file esterno js
				    inizializzaGrafici(datiConfigGrafici);
				});
				</script>
        </div>
    </div>

    <%-- ========================================================================= --%>
    <%-- 2) TAB: ECOSISTEMA & COMPONENTI                      --%>
    <%-- ========================================================================= --%>
    <div class="tab-pane fade" id="ecosistema" role="tabpanel" aria-labelledby="ecosistema-tab">
        <div class="row g-4 align-items-stretch"> <%-- align-items-stretch forza l'altezza uguale --%>
            
            <%-- COLONNA SINISTRA: COMPONENTI CRITICI --%>
            <div class="col-lg-6">
                <div class="card shadow-sm h-100"> <%-- h-100 estende la card --%>
                    <div class="card-header bg-white py-3 d-flex justify-content-between align-items-center">
                        <h5 class="mb-0 fw-bold text-primary"><i class="bi bi-cpu"></i> Stato Componenti Critici</h5>
                        <span class="badge bg-primary">Scadenza &lt; 30gg</span>
                    </div>
                    <div class="card-body p-0">
                        <div class="table-responsive">
                            <table class="table table-hover align-middle mb-0">
                                <thead class="table-light">
                                    <tr>
                                        <th class="ps-3">Nome</th>
                                        <th>Prezzo</th>
                                        <th>Manutenzione</th>
                                        <th class="text-center">Azione</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <% if (listaComponenti != null && !listaComponenti.isEmpty()) {
                                        for (Componente c : listaComponenti) { 
                                            int gg = c.getGiorniAllaManutenzione();
                                            String rigaClasse = "";
                                            if (c.isRotto()) rigaClasse = "table-danger";
                                            else if (c.isDifettoso()) rigaClasse = "table-warning";
                                            else if (gg <= 0) rigaClasse = "table-danger";
                                            else if (gg <= 7) rigaClasse = "table-warning";
                                    %>
                                        <tr class="<%= rigaClasse %>">
                                            <td class="ps-3">
                                                <div class="d-flex flex-column">
                                                    <span class="fw-bold text-uppercase" style="font-size: 0.9rem;"><%= c.getNome() %></span>
                                                    <div class="mt-1">
                                                        <% if(c.isRotto()) { %><span class="badge bg-danger"><i class="bi bi-x-circle"></i> ROTTO</span><% } %>
                                                        <% if(c.isDifettoso()) { %><span class="badge bg-warning text-dark"><i class="bi bi-exclamation-triangle"></i> DIFETTOSO</span><% } %>
                                                        <% if(c.isFineVita()) { %><span class="badge bg-dark"><i class="bi bi-hourglass-bottom"></i> VECCHIO</span><% } %>
                                                    </div>
                                                </div>
                                            </td>
                                            <td>€ <%= (c.getPrezzo() != null) ? String.format("%.2f", c.getPrezzo()) : "0.00" %></td>
                                            <td>
                                                <% if(c.isRotto()) { %><span class="text-danger fw-bold">Sostituzione immediata</span><% } else { %>
                                                    <span class="badge <%= (gg <= 0) ? "bg-danger" : (gg <= 7 ? "bg-warning text-dark" : "bg-success") %>">
                                                        <%= (gg <= 0) ? "MANUTENZIONE SCADUTA" : "Manutenzione tra " + gg + " gg" %>
                                                    </span>
                                                <% } %>
                                            </td>
                                            <td class="text-center"> 
                                                <a href="GestioneComponentiServlet?azione=manutenzione&id=<%= c.getId_componente() %>" class="btn btn-sm btn-success rounded shadow-sm" title="Esegui Manutenzione">
                                                    <i class="bi bi-tools"></i>
                                                </a>
                                            </td>
                                        </tr>
                                    <% } } else { %>
                                        <tr><td colspan="4" class="text-center py-4 text-muted">Nessun componente critico da monitorare.</td></tr>
                                    <% } %>
                                </tbody>
                            </table>
                        </div>
                    </div>
                    <div class="card-footer bg-white border-0 text-center pb-3">
                         <a href="GestioneComponentiServlet" class="btn btn-primary rounded-pill px-4 shadow-sm"><i class="bi bi-gear-wide-connected me-2"></i>GESTISCI TUTTI I COMPONENTI</a>
                    </div>
                </div>
            </div>

            <%-- COLONNA DESTRA: MONITORAGGIO BIOLOGICO (PESCI) --%>
            <div class="col-lg-6">
                <div class="card shadow-sm h-100"> <%-- h-100 estende la card --%>
                    <div class="card-header bg-info text-white py-3 d-flex justify-content-between align-items-center">
                        <h5 class="mb-0 fw-bold"><i class="bi bi-water"></i> Monitoraggio Biologico</h5>
                        <a href="GestionePesciServlet" class="btn btn-sm btn-light rounded-pill px-3 fw-bold">GESTISCI PESCI</a>
                    </div>
                    <div class="card-body p-0">
                        <div class="table-responsive">
                            <table class="table table-hover mb-0 align-middle">
                                <thead class="table-light">
                                    <tr>
                                        <th class="ps-3">Specie</th>
                                        <th>Alimentazione</th>
                                        <th>Temp. Range</th>
                                        <th>pH Range</th>
                                        <th>Densità Max</th>
                                        <th>Stress</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <% if (listaPesci != null && !listaPesci.isEmpty()) {
                                        for (Pesce p : listaPesci) { %>
                                        <tr>
                                            <td class="ps-3 fw-bold text-primary"><%= p.getSpecie() %></td>
                                            <td><span class="badge bg-secondary"><%= p.getTipoCibo() %></span></td>
                                            <td><i class="bi bi-thermometer-half text-danger"></i> <%= p.getTempMin() %> - <%= p.getTempMax() %>°C</td>
                                            <td><i class="bi bi-droplet-half text-primary"></i> <%= p.getPhMin() %> - <%= p.getPhMax() %></td>
                                            <td><%= p.getNumMaxMc() %> pesci/m³</td>
                                            <td class="text-muted small"><i><%= p.getSegnaliStress() %></i></td>
                                        </tr>
                                    <% } } else { %>
                                        <tr><td colspan="6" class="text-center py-4 text-muted">Nessuna specie registrata nella vasca.</td></tr>
                                    <% } %>
                                </tbody>
                            </table>
                        </div>
                    </div>
                    <%-- Footer fake aggiunto solo per pareggiare la struttura visiva con l'altra card --%>
                    <div class="card-footer bg-white border-0 py-4"></div>
                </div>
            </div>
            
        </div>
    </div>

    <%-- ========================================================================= --%>
    <%-- 3) TAB: PANNELLO DI CONTROLLO ESP32                                       --%>
    <%-- ========================================================================= --%>
    <div class="tab-pane fade" id="hardware" role="tabpanel" aria-labelledby="hardware-tab">
        <div class="card shadow-sm border-start border-4 border-dark">
            <div class="card-header bg-dark text-white py-3">
                <h5 class="mb-0 fw-bold"><i class="bi bi-gear-fill"></i> Pannello di Controllo (ESP32)</h5>
            </div>
            <div class="card-body">
                <form action="GestioneConfigurazioneServlet" method="POST">
                    <% for (it.acquario.model.Categoria cat : it.acquario.model.Categoria.values()) { 
                        boolean isTime = "TIME".equalsIgnoreCase(cat.name());
                    %>
                        <div class="col-12 mt-2 mb-2">
                            <h6 class="text-primary fw-bold border-bottom pb-1">
                                <i class="bi bi-tag-fill small"></i> <%= cat.name() %>
                            </h6>
                        </div>
                        <div class="row">
                            <% int contatoreParametri = 0;
                               if (listaC != null) {
                                   for (Configurazione c : listaC) { 
                                       if (c.getCategoria() != null && c.getCategoria().equals(cat)) { 
                                           contatoreParametri++;
                                           boolean isNumber = c.getTipoDato().toString().matches("INT|FLOAT");
                                           String stepVal = c.getTipoDato().toString().equals("FLOAT") ? "0.1" : "1";
                                           
                                           String chiave = c.getParametro();
                                           String valoreDaStampare = c.getValore().replace(",", ".");
                                           String stileDisabilitato = "";
                                           boolean isBiologico = false;

                                           if ("temp_min".equals(chiave) && request.getAttribute("bioTempMin") != null) {
                                               valoreDaStampare = String.valueOf(request.getAttribute("bioTempMin"));
                                               isBiologico = true;
                                           } else if ("temp_max".equals(chiave) && request.getAttribute("bioTempMax") != null) {
                                               valoreDaStampare = String.valueOf(request.getAttribute("bioTempMax"));
                                               isBiologico = true;
                                           } else if ("ph_min".equals(chiave) && request.getAttribute("bioPhMin") != null) {
                                               valoreDaStampare = String.valueOf(request.getAttribute("bioPhMin"));
                                               isBiologico = true;
                                           } else if ("ph_max".equals(chiave) && request.getAttribute("bioPhMax") != null) {
                                               valoreDaStampare = String.valueOf(request.getAttribute("bioPhMax"));
                                               isBiologico = true;
                                           }

                                           if (isTime) {
                                               stileDisabilitato = "readonly disabled style='background-color: #e9ecef; color: #6c757d; cursor: not-allowed; border-color: #ced4da !important;'";
                                           } else if (isBiologico) {
                                               stileDisabilitato = "readonly style='background-color: #e9ecef; color: #212529; cursor: not-allowed; font-weight: bold; border-color: #ced4da !important;' title='Calcolato automaticamente in base ai pesci in vasca'";
                                           }
                            %>
                                <div class="col-md-6 col-xl-4 mb-3">
                                    <label class="form-label small fw-bold text-secondary text-uppercase"><%= c.getDescrizione() %></label>
                                    <div class="input-group shadow-sm">
                                        <input type="<%= isNumber ? "number" : "text" %>" 
                                               step="<%= stepVal %>"
                                               name="<%= c.getParametro() %>" 
                                               value="<%= valoreDaStampare %>" 
                                               min="<%= c.getMinimo() %>" 
                                               max="<%= c.getMassimo() %>"
                                               class="form-control border-primary"
                                               <%= stileDisabilitato %>>
                                        
                                        <span class="input-group-text <%= (isTime || isBiologico) ? "bg-secondary-subtle text-secondary" : "bg-primary-subtle text-primary" %> fw-bold"><%= c.getTipoDato() %></span>
                                    </div>
                                </div>
                            <% } } } %>
                            <% if (contatoreParametri == 0) { %>
                                <div class="col-12"><small class="text-muted italic">Nessun parametro trovato.</small></div>
                            <% } %>
                        </div>
                    <% } %>
                    
                    <div class="text-end mt-4">
                        <a href="GestioneConfigurazioneServlet" class="btn btn-outline-secondary px-4 py-2 rounded-pill shadow me-2">
                            <i class="bi bi-pencil-square me-1"></i> ⚙️ Gestisci tutte le configurazioni
                        </a>
                        <button type="submit" class="btn btn-primary px-4 py-2 rounded-pill shadow">
                            <i class="bi bi-cloud-arrow-up-fill me-2"></i>Sincronizza con Acquario
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

		   <%-- ========================================================================= --%>
		    <%-- 4) TAB: DIAGNOSTICA E MESSAGGI RAW (COMPLETATO)                          --%>
		    <%-- ========================================================================= --%>
		    <div class="tab-pane fade" id="diagnostica" role="tabpanel" aria-labelledby="diagnostica-tab">
		        <div class="card shadow-sm border-start border-4 border-dark">
				    <div class="card-header bg-dark text-white py-3 d-flex justify-content-between align-items-center">
				        <h5 class="mb-0 fw-bold"><i class="bi bi-terminal-box me-2"></i>Console Log Diagnostica & Registro RAW</h5>
				        <span class="badge bg-secondary">Ultimi 10 Eventi Automatici</span>
				    </div>
				    <div class="card-body p-0">
				        <div class="table-responsive">
				            <table class="table table-hover align-middle mb-0 text-center">
				                <thead class="table-light">
				                    <tr>
				                        <th style="width: 15%;">Orario</th>
				                        <th style="width: 15%;">Frequenza Controllo</th>
				                        <th style="width: 10%;">Esito</th>
				                        <th style="width: 15%;">Origine</th>
				                        <th style="width: 30%;" class="text-start">Messaggio di Sistema / Payload JSON</th>
				                        <th style="width: 15%;">Stato Automazione</th>
				                    </tr>
				                </thead>
				                <tbody>
								    <c:choose>
								        <%-- CASO 1: Ci sono messaggi nel database -> Cicla e crea una riga <tr> per OGNI messaggio --%>
								        <c:when test="${not empty listaMessaggi}">
								            <c:forEach var="msg" items="${listaMessaggi}">
								                <tr>
								                    <td class="text-muted fw-semibold" style="font-size: 0.9rem;">
								                        <fmt:formatDate value="${msg.timestamp}" pattern="dd MMM yyyy - HH:mm" />
								                    </td>
								                    
								                    <td>
								                        <span class="badge rounded-pill bg-light text-dark border px-3 py-2">
								                            <c:out value="${msg.tipoControllo}" />
								                        </span>
								                    </td>
								                    
								                    <td>
								                        <c:choose>
								                            <c:when test="${msg.esito == 'ALLARME'}">
								                                <span class="badge bg-danger text-uppercase px-2 py-1.5"><i class="bi bi-exclamation-triangle-fill me-1"></i>ALLARME</span>
								                            </c:when>
								                            <c:when test="${msg.esito == 'OK'}">
								                                <span class="badge bg-success text-uppercase px-2 py-1.5"><i class="bi bi-check-circle-fill me-1"></i>OK</span>
								                            </c:when>
								                            <c:otherwise>
								                                <span class="badge bg-info text-dark text-uppercase px-2 py-1.5"><i class="bi bi-info-circle-fill me-1"></i>INFO</span>
								                            </c:otherwise>
								                        </c:choose>
								                    </td>
								                    
								                    <td>
								                        <c:choose>
								                            <c:when test="${msg.origine == 'UTENTE'}">
								                                <span class="text-primary fw-semibold" style="font-size: 0.9rem;">
								                                    <i class="bi bi-person-badge-fill me-1"></i> Interfaccia Utente
								                                </span>
								                            </c:when>
								                            <c:otherwise>
								                                <span class="text-secondary fw-semibold" style="font-size: 0.9rem;">
								                                    <i class="bi bi-cpu-fill me-1"></i> Hardware ESP32
								                                </span>
								                            </c:otherwise>
								                        </c:choose>
								                    </td>
								                    
								                    <td class="text-start text-break font-monospace" style="font-size: 0.82rem;">
								                        <span class="bg-light p-2 rounded d-block border border-light-subtle text-dark">
								                            <c:out value="${msg.messaggio}" />
								                        </span>
								                    </td>
								                    
								                    <td>
								                        <c:choose>
								                            <c:when test="${msg.elaborato}">
								                                <span class="badge bg-light text-success border border-success fw-bold py-1.5 px-2">
								                                    <i class="bi bi-gear-fill me-1"></i> Eseguito
								                                </span>
								                            </c:when>
								                            <c:otherwise>
								                                <span class="badge bg-light text-warning border border-warning fw-bold py-1.5 px-2 text-uppercase">
								                                    <i class="bi bi-hourglass-split me-1"></i> In Coda...
								                                </span>
								                            </c:otherwise>
								                        </c:choose>
								                    </td>
								                </tr>
								            </c:forEach>
								        </c:when>
								        
								        <%-- CASO 2: La lista è vuota -> Mostra una sola riga di avviso larga quanto tutta la tabella --%>
								        <c:otherwise>
								            <tr>
								                <td colspan="6" class="text-muted py-5">
								                    <i class="bi bi-file-earmark-code d-block h3 mb-2 text-secondary"></i>
								                    Nessun record diagnostico rilevato nel database.
								                </td>
								            </tr>
								        </c:otherwise>
								    </c:choose>
								</tbody>
				            </table>
				        </div>
				    </div>
				</div>
		    </div>

</div>

 <%-- CHATBOT FLUTTUANTE --%>
    <div id="chat-launcher" onclick="toggleChat()" style="display: block;">
        <button class="btn btn-primary shadow-lg d-flex align-items-center justify-content-center">
            <i class="bi bi-chat-dots-fill fs-4"></i>
        </button>
    </div>

    <div id="chat-container" style="display: none;">
        <div class="card shadow-lg">
            <div class="card-header bg-dark text-white d-flex justify-content-between align-items-center">
                <h6 class="mb-0"><i class="bi bi-robot text-info me-2"></i>Assistente</h6>
                <button type="button" class="btn-close btn-close-white" onclick="toggleChat()"></button>
            </div>
            
            <div id="chat-window" class="d-flex flex-column">
                <%-- Messaggi --%>
            </div>
            
            <div class="chat-input-group">
                <div class="d-flex gap-1 mb-2">
                    <button class="btn btn-xs btn-outline-primary py-0 px-2" style="font-size: 0.7rem;" onclick="sendMessage('home_')">Home</button>
                    <button class="btn btn-xs btn-outline-primary py-0 px-2" style="font-size: 0.7rem;" onclick="sendMessage('stato_')">Stato</button>
                    <button class="btn btn-xs btn-outline-primary py-0 px-2" style="font-size: 0.7rem;" onclick="sendMessage('energia_')">Stato energia</button>
                    <button class="btn btn-xs btn-outline-primary py-0 px-2" style="font-size: 0.7rem;" onclick="sendMessage('grazie_')">Grazie</button>
                    
                    <button class="btn btn-sm btn-outline-danger border-0 py-0" onclick="localStorage.removeItem('acquaponica_chat_history'); location.reload();">
                        <i class="bi bi-trash"></i>
                    </button>
                </div>
                <div class="input-group input-group-sm">
                    <input type="text" id="userInput" class="form-control" placeholder="Chiedi...">
                    <button class="btn btn-primary" onclick="sendMessage(document.getElementById('userInput').value)">Invia</button>
                </div>
            </div>
        </div>
    </div>

    <script src="js/chatbot.js"></script>
    <script>
        window.addEventListener('pageshow', function(event) {
            if (event.persisted || (window.performance && window.performance.navigation.type === 2)) {
                window.location.reload();
            }
        });
    </script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
   