<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, it.acquario.model.Allarme" %>
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);
%>
<!DOCTYPE html>
<html>
<head>
    <title>Storico Notifiche - SistemaAcquario</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css">
</head>
<body class="bg-light">
    <%@ include file="messaggi.jsp" %>
    
    <nav class="navbar navbar-dark bg-danger mb-4 shadow">
        <div class="container">
            <a class="navbar-brand fw-bold" href="DashboardServlet">
                <i class="bi bi-arrow-left-circle me-2"></i> Torna alla Dashboard
            </a>
            <span class="navbar-text text-white">Centro Notifiche</span>
        </div>
    </nav>

    <div class="container mb-5">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2 class="fw-bold"><i class="bi bi-journal-text"></i> Registro Eventi</h2>
            
            <div class="d-flex gap-2">
                <button type="button" class="btn btn-primary btn-sm rounded-pill px-3 shadow-sm fw-bold" data-bs-toggle="modal" data-bs-target="#modalNuovoAllarme">
                    <i class="bi bi-plus-circle me-1"></i> Nuovo Allarme
                </button>

                <% 
                   List<Allarme> storico = (List<Allarme>) request.getAttribute("listaStoricoAllarmi");
                   boolean ciSonoNonLetti = false;
                   if(storico != null) {
                       for(Allarme a : storico) if(!a.isLetta()) { ciSonoNonLetti = true; break; }
                   }
                   if(ciSonoNonLetti) {
                %>
                   <form action="StoricoAllarmiServlet" method="POST" class="m-0">
                        <input type="hidden" name="azione" value="leggiTutto">
                        <button type="submit" class="btn btn-outline-primary btn-sm rounded-pill px-3 shadow-sm">
                            <i class="bi bi-check2-all"></i> Segna tutti come letti
                        </button>
                    </form>
                <% } %>
            </div>
        </div>

        <div class="card shadow-sm border-0">
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead class="table-dark">
                        <tr>
                            <th class="ps-3">Stato</th>
                            <th>Livello</th>
                            <th>Data e Ora</th>
                            <th>Messaggio</th>
                            <th>Componente</th>
                            <th class="text-center">Azione</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% 
                        if (storico != null && !storico.isEmpty()) {
                            for (Allarme a : storico) { 
                                String colorClass = a.getClasseColore();
                                String opacita = (a.isLetta()) ? "text-muted opacity-75" : "fw-bold";
                        %>
                            <tr class="<%= opacita %>">
                                <td class="ps-3">
                                    <% if(!a.isLetta()) { %>
                                        <i class="bi bi-circle-fill text-primary" title="Nuovo"></i>
                                    <% } else { %>
                                        <i class="bi bi-check-lg text-success" title="Letto"></i>
                                    <% } %>
                                </td>
                                <td>
                                    <span class="badge <%= colorClass %>"><%= a.getLivello() %></span>
                                </td>
                                <td class="small text-nowrap">
                                    <%= new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(a.getData_ora()) %>
                                </td>
                                <td class="w-50">
                                    <%= a.getMessaggio() %>
                                    <% if(a.isRisolto()) { %>
                                        <span class="badge bg-success ms-1 small"><i class="bi bi-patch-check"></i> Risolto</span>
                                    <% } %>
                                </td>
                                <td>
                                    <span class="badge bg-secondary">
                                        <%= a.getNomeComponente() != null ? a.getNomeComponente() : "#" + a.getId_componente_rif() %>
                                    </span>
                                </td>
                                
                                <td class="text-center">
                                    <div class="d-flex justify-content-center gap-1">
                                        <% if(!a.isLetta()) { %>
                                            <form action="StoricoAllarmiServlet" method="POST" class="m-0">
                                                <input type="hidden" name="azione" value="leggiSingolo">
                                                <input type="hidden" name="id" value="<%= a.getId_allarme() %>">
                                                <button type="submit" class="btn btn-sm btn-outline-primary" title="Segna come letto">
                                                    <i class="bi bi-eye"></i>
                                                </button>
                                            </form>
                                        <% } %>
                                
                                        <% if(!a.isRisolto() && !a.getLivello().name().equals("INFO")) { %>
                                            <form action="StoricoAllarmiServlet" method="POST" class="m-0">
                                                <input type="hidden" name="azione" value="risolviSingolo">
                                                <input type="hidden" name="id" value="<%= a.getId_allarme() %>">
                                                <button type="submit" class="btn btn-sm btn-success" title="Risolvi Problema">
                                                    <i class="bi bi-wrench"></i>
                                                </button>
                                            </form>
                                        <% } %>
                                        
                                        <button type="button" class="btn btn-sm btn-outline-warning" title="Modifica Record" data-bs-toggle="modal" data-bs-target="#modalModifica<%= a.getId_allarme() %>">
                                            <i class="bi bi-pencil"></i>
                                        </button>

                                        <form action="StoricoAllarmiServlet" method="POST" class="m-0" onsubmit="return confirm('Vuoi eliminare definitivamente questo allarme dallo storico?');">
                                            <input type="hidden" name="azione" value="elimina">
                                            <input type="hidden" name="id" value="<%= a.getId_allarme() %>">
                                            <button type="submit" class="btn btn-sm btn-outline-danger" title="Elimina definitivamente">
                                                <i class="bi bi-trash"></i>
                                            </button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        <% 
                            } 
                        } else { 
                        %>
                            <tr>
                                <td colspan="6" class="text-center py-5 text-muted fst-italic">
                                    Il registro degli allarmi è vuoto.
                                </td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <div class="modal fade" id="modalNuovoAllarme" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <form action="StoricoAllarmiServlet" method="POST">
                    <input type="hidden" name="azione" value="inserisci">
                    <div class="modal-header bg-dark text-white">
                        <h5 class="modal-title fw-bold"><i class="bi bi-bell-fill text-warning me-2"></i>Genera Allarme Manuale</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body text-start">
                        <div class="mb-3">
                            <label class="form-label small fw-bold">Livello Gravità</label>
                            <select name="livello" class="form-select" required>
                                <option value="INFO">INFO</option>
                                <option value="WARNING">WARNING</option>
                                <option value="CRITICAL">CRITICAL</option>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label class="form-label small fw-bold">Data e Ora Evento</label>
                            <%-- Genera la data attuale preimpostata nel formato HTML5 --%>
                            <% String dataOggiHtml = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm").format(new java.util.Date()); %>
                            <input type="datetime-local" name="data_ora" class="form-control" value="<%= dataOggiHtml %>" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label small fw-bold">Messaggio Segnalazione</label>
                            <textarea name="messaggio" class="form-control" rows="3" placeholder="Descrivi l'anomalia..." required></textarea>
                        </div>
                        <div class="mb-3">
                            <label class="form-label small fw-bold">ID Componente Riferimento (`id_componente_rif`)</label>
                            <input type="number" name="id_componente_rif" class="form-control" placeholder="Es. 1" required>
                        </div>
                        
                        <hr class="my-3">
                        
                        <div class="form-check form-switch mb-2">
                            <input class="form-check-input" type="checkbox" name="risolto" id="insRis">
                            <label class="form-check-label small fw-bold" for="insRis">Nasce già Risolto (1)</label>
                        </div>
                        <div class="form-check form-switch">
                            <input class="form-check-input" type="checkbox" name="letta" id="insLet">
                            <label class="form-check-label small fw-bold" for="insLet">Nasce già Letto/Archiviato (1)</label>
                        </div>
                    </div>
                    <div class="modal-footer bg-light">
                        <button type="button" class="btn btn-sm btn-secondary" data-bs-dismiss="modal">Annulla</button>
                        <button type="submit" class="btn btn-sm btn-danger px-3">Inietta Allarme</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <% 
    if (storico != null && !storico.isEmpty()) {
        for (Allarme a : storico) { 
            // Formattiamo la data memorizzata nel record per l'input HTML5
            String dataRecordHtml = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm").format(a.getData_ora());
    %>
        <div class="modal fade" id="modalModifica<%= a.getId_allarme() %>" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <form action="StoricoAllarmiServlet" method="POST">
                        <input type="hidden" name="azione" value="modifica">
                        <input type="hidden" name="id" value="<%= a.getId_allarme() %>">
                        <div class="modal-header bg-warning text-dark">
                            <h5 class="modal-title fw-bold"><i class="bi bi-pencil-square me-2"></i>Modifica Allarme #<%= a.getId_allarme() %></h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body text-start">
                            <div class="mb-3">
                                <label class="form-label small fw-bold">Livello</label>
                                <select name="livello" class="form-select">
                                    <option value="INFO" <%= a.getLivello().name().equals("INFO") ? "selected" : "" %>>INFO</option>
                                    <option value="WARNING" <%= a.getLivello().name().equals("WARNING") ? "selected" : "" %>>WARNING</option>
                                    <option value="CRITICAL" <%= a.getLivello().name().equals("CRITICAL") ? "selected" : "" %>>CRITICAL</option>
                                </select>
                            </div>
                            <div class="mb-3">
                                <label class="form-label small fw-bold">Data e Ora Registrata</label>
                                <input type="datetime-local" name="data_ora" class="form-control" value="<%= dataRecordHtml %>" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label small fw-bold">Messaggio</label>
                                <input type="text" name="messaggio" class="form-control" value="<%= a.getMessaggio() %>" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label small fw-bold">ID Componente Riferimento (`id_componente_rif`)</label>
                                <input type="number" name="id_componente_rif" class="form-control" value="<%= a.getId_componente_rif() %>" required>
                            </div>
                            
                            <hr class="my-3">
                            
                            <div class="form-check form-switch mb-2">
                                <input class="form-check-input" type="checkbox" name="risolto" id="swRis<%= a.getId_allarme() %>" <%= a.isRisolto() ? "checked" : "" %>>
                                <label class="form-check-label small fw-bold" for="swRis<%= a.getId_allarme() %>">Contrassegna come Risolto (1)</label>
                            </div>
                            <div class="form-check form-switch">
                                <input class="form-check-input" type="checkbox" name="letta" id="swLet<%= a.getId_allarme() %>" <%= a.isLetta() ? "checked" : "" %>>
                                <label class="form-check-label small fw-bold" for="swLet<%= a.getId_allarme() %>">Archivia come Letto (1)</label>
                            </div>
                        </div>
                        <div class="modal-footer bg-light">
                            <button type="button" class="btn btn-sm btn-secondary" data-bs-dismiss="modal">Chiudi</button>
                            <button type="submit" class="btn btn-sm btn-warning fw-bold px-3">Salva Modifiche</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    <% 
        } 
    } 
    %>
    
    <script>
        (function() {
            window.addEventListener('pageshow', function(event) {
               if (event.persisted || (window.performance && window.performance.navigation.type === 2)) {
                    document.body.style.display = "none";
                    window.location.replace(window.location.href);
                }
            });
    
            document.addEventListener("DOMContentLoaded", function() {
                setTimeout(function() {
                    const alerts = document.querySelectorAll('.alert');
                    alerts.forEach(function(alert) {
                        const bsAlert = new bootstrap.Alert(alert);
                        bsAlert.close();
                    });
                }, 3000);
            });
        })();
    </script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>