<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="it.acquario.model.Configurazione" %>
<%
    // Recupero la lista totale e l'eventuale configurazione da modificare passata dalla servlet
    List<Configurazione> lista = (List<Configurazione>) request.getAttribute("listaConfigurazioni");
    Configurazione configInModifica = (Configurazione) request.getAttribute("configurazioneSelezionata");
    
    // Capiamo se la pagina è stata ricaricata per mostrare subito la modale di modifica
    boolean apriModificaSubito = (configInModifica != null);
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Pannello Configurazione Sistema</title>
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    
    <style>
        .container { width: 90%; margin: 20px auto; font-family: 'Segoe UI', sans-serif; }
        .table-custom { width: 100%; border-collapse: collapse; margin-top: 20px; background: white; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
        .table-custom th, .table-custom td { padding: 12px; border-bottom: 1px solid #ddd; text-align: left; }
        .table-custom th { background-color: #34495e; color: white; }
        .btn-action { padding: 6px 12px; border-radius: 4px; text-decoration: none; color: white; font-size: 14px; margin-left: 5px; display: inline-block; border: none; cursor: pointer; }
        .btn-edit { background-color: #f39c12; }
        .btn-delete { background-color: #e74c3c; }
        .header-page { display: flex; justify-content: space-between; align-items: center; }
        .btn-add { background-color: #2ecc71; padding: 10px 20px; color: white; text-decoration: none; border-radius: 20px; font-weight: bold; border: none; cursor: pointer; }
        .badge-type { background: #dbeafe; color: #1e40af; font-weight: bold; font-size: 11px; padding: 4px 8px; border-radius: 4px; }
        a { text-decoration: none; }
    </style>
</head>
<body style="background-color: #f8f9fa;">

    <jsp:include page="messaggi.jsp" />

    <div class="container">
        <div class="header-page">
            <div style="margin-bottom: 5px;">
                <a href="DashboardServlet" style="color: #6c757d; font-size: 20px; font-weight: 500; transition: color 0.3s;"
                   onmouseover="this.style.color='#0d6efd'" onmouseout="this.style.color='#6c757d'">
                    <i class="bi bi-arrow-left-circle me-2" style="font-size: 1.2rem; vertical-align: middle; color: black;"></i> 
                    Torna alla Dashboard
                </a>
            </div>
            <h1>Parametri di Configurazione hardware</h1>
            
            <button type="button" class="btn btn-add text-white" data-bs-toggle="modal" data-bs-target="#modalNuovaConfig">
                <i class="bi bi-gear-plus me-1"></i> Nuovo Parametro
            </button>
        </div>

        <table class="table-custom">
            <thead>
                <tr>
                    <th>Descrizione Parametro</th>
                    <th>Chiave (Codice ESP32)</th>
                    <th>Valore Attuale</th>
                    <th>Categoria</th>
                    <th>Tipo Dato</th>
                    <th style="text-align: right; white-space: nowrap;">Azioni</th>
                </tr>
            </thead>
            <tbody>
                <% 
                    if (lista != null) { 
                        for (Configurazione conf : lista) { 
                            boolean isTime = "TIME".equalsIgnoreCase(conf.getCategoria().name());
                            
                            // IDENTIFICHIAMO SE È UN PARAMETRO BIOLOGICO PROTETTO
                            String chiaveP = conf.getParametro();
                            boolean isBio = "temp_min".equals(chiaveP) || "temp_max".equals(chiaveP) || 
                                            "ph_min".equals(chiaveP) || "ph_max".equals(chiaveP);
                %>
                <tr>
                    <td><strong><%= conf.getDescrizione() %></strong></td>
                    <td><code><%= conf.getParametro() %></code></td>
                    <td><mark class="px-2 rounded" style="background-color: #f1f2f6;"><%= conf.getValore() %></mark></td>
                    <td><span class="badge bg-secondary text-uppercase"><%= conf.getCategoria() %></span></td>
                    <td><span class="badge-type"><%= conf.getTipoDato() %></span></td>
                    <td style="text-align: right; white-space: nowrap;">
                        <% if (!isBio) { %>
				            <a href="GestioneConfigurazioneServlet?azione=modificaForm&id=<%= conf.getParametro() %>" class="btn-action btn-edit" title="Modifica">✏️</a>
				        <% } %>
                        <% if (!isTime && !isBio) { // Blocco eliminazione se TIME o BIO %>
                            <a href="GestioneConfigurazioneServlet?azione=elimina&id=<%= conf.getParametro() %>" class="btn-action btn-delete" 
                               onclick="return confirm('Sei sicuro di voler eliminare definitivamente il parametro <%= conf.getParametro() %>?')" title="Elimina">🗑️</a>
                        <% } else { %>
                            <button class="btn-action bg-body-secondary text-muted" style="cursor: not-allowed;" disabled 
                                    title="<%= isTime ? "I registri temporali di sistema (TIME) non possono essere eliminati" : "I parametri biologici protetti non possono essere eliminati" %>">🔒</button>
                        <% } %>
                    </td>
                </tr>
                <% 
                        } 
                    } 
                %>
            </tbody>
        </table>
    </div>

    <div class="modal fade" id="modalNuovaConfig" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content" style="border-radius: 12px; overflow: hidden; border: none; box-shadow: 0 10px 30px rgba(0,0,0,0.3);">
                <div class="modal-header text-white" style="background-color: #2b2e33; padding: 15px 20px;">
                    <h5 class="modal-title" style="font-size: 16px; font-weight: 600; display: flex; align-items: center;">
                        <i class="bi bi-sliders text-success me-2" style="font-size: 1.2rem;"></i> Aggiungi Nuova Configurazione
                    </h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <form action="GestioneConfigurazioneServlet" method="POST">
                    <input type="hidden" name="azione" value="inserisci">
                    <div class="modal-body" style="padding: 20px; background-color: #ffffff;">
                        <div class="mb-3">
                            <label class="form-label fw-bold text-secondary" style="font-size: 13px;">Nome / Descrizione Estesa</label>
                            <input type="text" name="descrizione" class="form-control" style="border-radius: 6px;" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-bold text-secondary" style="font-size: 13px;">Chiave Codice Parametro</label>
                            <input type="text" name="parametro" class="form-control" style="border-radius: 6px;" placeholder="TUTTO_MAIUSCOLO_CON_UNDERSCORE" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-bold text-secondary" style="font-size: 13px;">Valore Iniziale</label>
                            <input type="text" name="valore" class="form-control" style="border-radius: 6px;" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-bold text-secondary" style="font-size: 13px;">Categoria de Appartenenza</label>
                            <select name="categoria" class="form-select" style="border-radius: 6px;">
                                <option value="ACQUARIO">ACQUARIO</option>
                                <option value="SERRA">SERRA</option>
                                <option value="SISTEMA">SISTEMA</option>
                                <option value="TIME">TIME</option>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-bold text-secondary" style="font-size: 13px;">Tipo di Dato Atteso</label>
                            <select name="tipo_dato" class="form-select" style="border-radius: 6px;">
                                <option value="FLOAT">FLOAT</option>
                                <option value="INT">INT</option>
                                <option value="STRING">STRING</option>
                            </select>
                        </div>
                    </div>
                    <div class="modal-footer" style="background-color: #f8f9fa; border-top: 1px solid #eeeeee; padding: 12px 20px;">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" style="border: none; border-radius: 6px; font-weight: 600; padding: 6px 18px;">Annulla</button>
                        <button type="submit" class="btn btn-success text-white" style="border: none; border-radius: 6px; font-weight: 600; padding: 6px 18px;">Crea Parametro</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <% if (apriModificaSubito) { %>
    <div class="modal fade" id="modalModificaConfig" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content" style="border-radius: 12px; overflow: hidden; border: none; box-shadow: 0 10px 30px rgba(0,0,0,0.3);">
                
                <div class="modal-header text-white" style="background-color: #2b2e33; padding: 15px 20px;">
                    <h5 class="modal-title" style="font-size: 16px; font-weight: 600; display: flex; align-items: center;">
                        <i class="bi bi-pencil-square text-warning me-2" style="font-size: 1.2rem;"></i> Modifica Parametro Avanzata
                    </h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-toggle="modal" onclick="window.location.href='GestioneConfigurazioneServlet';" aria-label="Close"></button>
                </div>
                
                <form action="GestioneConfigurazioneServlet" method="POST">
                    <input type="hidden" name="azione" value="aggiorna">
                    <input type="hidden" name="parametro" value="<%= configInModifica.getParametro() %>">
                    
                    <div class="modal-body" style="padding: 20px; background-color: #ffffff;">
                        
                        <div class="mb-3">
                            <label class="form-label fw-bold text-secondary" style="font-size: 13px;">Chiave di sistema (Codice ESP32)</label>
                            <input type="text" class="form-control bg-body-secondary text-muted" value="<%= configInModifica.getParametro() %>" disabled style="cursor: not-allowed; font-family: monospace;">
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold text-secondary" style="font-size: 13px;">Descrizione Parametro</label>
                            <input type="text" name="descrizione" value="<%= configInModifica.getDescrizione() %>" class="form-control" style="border-radius: 6px;" required>
                        </div>
                        
                        <div class="mb-3">
                            <label class="form-label fw-bold text-secondary" style="font-size: 13px;">Valore Parametro</label>
                            <% 
                                boolean isTime = "TIME".equalsIgnoreCase(configInModifica.getCategoria().name()); 
                                // BLOCCO INPUT BOX VALORE SE È UNO DEI 4 PARAMETRI BIOLOGICI
                                String chMod = configInModifica.getParametro();
                                boolean isBioMod = "temp_min".equals(chMod) || "temp_max".equals(chMod) || 
                                                   "ph_min".equals(chMod) || "ph_max".equals(chMod);
                            %>
                            <input type="text" name="valore" value="<%= configInModifica.getValore() %>" 
                                   class="form-control" style="border-radius: 6px;" 
                                   <%= (isTime || isBioMod) ? "readonly style='background-color: #e9ecef; color: #6c757d; cursor: not-allowed; font-weight: bold;'" : "" %> 
                                   <%= isBioMod ? "title='Valore calcolato automaticamente dall Anagrafe Biologica dei pesci'" : "" %> required>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold text-secondary" style="font-size: 13px;">Categoria</label>
                            <select name="categoria" class="form-select" style="border-radius: 6px;">
                                <% for (it.acquario.model.Categoria cat : it.acquario.model.Categoria.values()) { %>
                                    <option value="<%= cat.name() %>" <%= cat.equals(configInModifica.getCategoria()) ? "selected" : "" %>>
                                        <%= cat.name() %>
                                    </option>
                                <% } %>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold text-secondary" style="font-size: 13px;">Tipo Dato</label>
                            <select name="tipo_dato" class="form-select" style="border-radius: 6px;">
                                <% for (it.acquario.model.TipoDato tipo : it.acquario.model.TipoDato.values()) { %>
                                    <option value="<%= tipo.name() %>" <%= tipo.equals(configInModifica.getTipoDato()) ? "selected" : "" %>>
                                        <%= tipo.name() %>
                                    </option>
                                <% } %>
                            </select>
                        </div>
                        
                        <div class="p-3 rounded text-secondary" style="background-color: #f1f2f6; font-size: 12px; border-left: 4px solid #34495e;">
                            <i class="bi bi-info-circle-fill text-primary me-1"></i> 
                            <% if (isBioMod) { %>
                                <strong>Parametro Biologico Vincolato:</strong> Il valore numerico viene gestito in automatico dall'Anagrafe Pesci. Da questa schermata puoi modificarne solo la Categoria o la Descrizione.
                            <% } else { %>
                                La modifica di questi metadati si rifletterà istantaneamente sui filtri della Webapp. Assicurati che i tipi dato e le categorie corrispondano alle aspettative dell'hardware.
                            <% } %>
                        </div>
                    </div>
                    
                    <div class="modal-footer" style="background-color: #f8f9fa; border-top: 1px solid #eeeeee; padding: 12px 20px;">
                        <button type="button" class="btn btn-secondary" onclick="window.location.href='GestioneConfigurazioneServlet';" style="border: none; border-radius: 6px; font-weight: 600; padding: 6px 18px;">Annulla</button>
                        <button type="submit" class="btn btn-warning text-white" style="border: none; border-radius: 6px; font-weight: 600; padding: 6px 18px;">Aggiorna Configurazione</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
    <% } %>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
    document.addEventListener("DOMContentLoaded", function() {
        <% if (apriModificaSubito) { %>
            var myModal = new bootstrap.Modal(document.getElementById('modalModificaConfig'), {
                backdrop: 'static',
                keyboard: false
            });
            myModal.show();

            if (window.history.replaceState) {
                window.history.replaceState(null, null, "GestioneConfigurazioneServlet");
            }
        <% } %>
    });
    </script>
</body>
</html>