<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="it.acquario.model.Componente" %>
<%
    // Recupero la lista dei componenti e l'eventuale componente da modificare
    List<Componente> lista = (List<Componente>) request.getAttribute("listaCompleta");
    Componente compInModifica = (Componente) request.getAttribute("componente");
    
    // Capiamo se la pagina è stata ricaricata per mostrare la modifica
    boolean apriModificaSubito = (compInModifica != null);
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Gestione Inventario Acquario</title>
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
        .btn-maint { background-color: #2ecc71; }
        .header-page { display: flex; justify-content: space-between; align-items: center; }
        .btn-add { background-color: #0d6efd; padding: 10px 20px; color: white; text-decoration: none; border-radius: 20px; font-weight: bold; border: none; cursor: pointer; }
    	.badge { padding: 4px 8px; border-radius: 4px; color: white; font-size: 11px; font-weight: bold; margin-right: 2px; }
    	.badge-red { background: #e74c3c; }
    	.badge-orange { background: #f39c12; }
    	.badge-gray { background: #95a5a6; }
    	.badge-green { background: #2ecc71; }
        
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
        
            <h1>Inventario Completo Componenti</h1>
            
            <button type="button" class="btn btn-primary btn-add" data-bs-toggle="modal" data-bs-target="#modalNuovoComponente">
                <i class="bi bi-plus-circle me-1"></i> Nuovo Componente
            </button>
        </div>
		
        <table class="table-custom">
            <thead>
                <tr>
                    <th>Componente</th>
                    <th>Info Acquisto</th>
                    <th>Stato Salute</th>
                    <th>Manutenzione</th>
                    <th>Azioni</th>
                </tr>
            </thead>
              
            <tbody>
                <%
                    if (lista != null) {
                        for (Componente comp : lista) {
                            java.time.LocalDate fineVita = comp.getData_acquisto().toLocalDate().plusMonths(comp.getVita_media_mesi());
                            boolean isVecchio = java.time.LocalDate.now().isAfter(fineVita);
                %>
                <tr>
                    <td>
                        <strong><%= comp.getNome() %></strong><br>
                        <small>€ <%= comp.getPrezzo() %></small>
                    </td>
                    <td>
                        Acquistato: <%= comp.getData_acquisto() %><br>
                        <small>Fine vita: <%= fineVita %></small>
                    </td>
                    <td>
                        <% if(comp.isRotto()) { %><span class="badge badge-red">ROTTO</span><% } %>
                        <% if(comp.isDifettoso()) { %><span class="badge badge-orange">DIFETTOSO</span><% } %>
                        <% if(isVecchio) { %><span class="badge badge-gray">VECCHIO</span><% } %>
                        <% if(!comp.isRotto() && !comp.isDifettoso() && !isVecchio) { %><span class="badge badge-green">OK</span><% } %>
                    </td>
                    <td>
                        Ultima: <%= comp.getUltima_manutenzione() %><br>
                        <small>Ogni <%= comp.getManutenzione_ogni_giorni() %> gg</small>
                    </td>
                   
                    <td style="text-align: right; white-space: nowrap;">
                        <a href="GestioneComponentiServlet?azione=manutenzione&id=<%= comp.getId_componente() %>" 
                           class="btn-action btn-maint" title="Esegui Manutenzione">🔧</a>
                        
                        <a href="GestioneComponentiServlet?azione=modificaForm&id=<%= comp.getId_componente() %>&from=gestione" 
                           class="btn-action btn-edit" title="Modifica">✏️</a>
                        
                        <a href="GestioneComponentiServlet?azione=elimina&id=<%= comp.getId_componente() %>" 
                           class="btn-action btn-delete" 
                           onclick="return confirm('Sei sicuro di voler eliminare <%= comp.getNome().replace("'", "\\'") %>?')" 
                           title="Elimina">🗑️</a>
                    </td>
                </tr>
                <% 
                        } 
                    } 
                %>
            </tbody>
        </table>       
    </div>

    <div class="modal fade" id="modalNuovoComponente" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content" style="border-radius: 12px; overflow: hidden; border: none; box-shadow: 0 10px 30px rgba(0,0,0,0.3);">
                
                <div class="modal-header text-white" style="background-color: #2b2e33; padding: 15px 20px;">
                    <h5 class="modal-title" style="font-size: 16px; font-weight: 600; display: flex; align-items: center;">
                        <i class="bi bi-plus-circle text-primary me-2" style="font-size: 1.2rem;"></i> Aggiungi Nuovo Componente
                    </h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                
                <form action="GestioneComponentiServlet" method="POST">
                    <input type="hidden" name="azione" value="inserisci">
                    
                    <div class="modal-body" style="padding: 20px; background-color: #ffffff;">
                        <div class="mb-3">
                            <label class="form-label fw-bold text-secondary" style="font-size: 13px;">Nome Componente</label>
                            <input type="text" name="nome" class="form-control" placeholder="Es. Pompa di Risalita" style="border-radius: 6px;" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-bold text-secondary" style="font-size: 13px;">Prezzo (€)</label>
                            <input type="number" step="0.01" name="prezzo" class="form-control" placeholder="0.00" style="border-radius: 6px;" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-bold text-secondary" style="font-size: 13px;">Data Acquisto</label>
                            <input type="date" name="data_acquisto" class="form-control" style="border-radius: 6px;" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-bold text-secondary" style="font-size: 13px;">Intervallo Manutenzione (Giorni)</label>
                            <input type="number" name="manutenzione_ogni_giorni" class="form-control" placeholder="Es. 30" style="border-radius: 6px;" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-bold text-secondary" style="font-size: 13px;">Vita Media Stimata (Mesi)</label>
                            <input type="number" name="vita_media_mesi" class="form-control" placeholder="Es. 24" style="border-radius: 6px;" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-bold text-secondary" style="font-size: 13px;">Data Ultima Manutenzione (Opzionale)</label>
                            <input type="date" name="ultima_manutenzione" class="form-control" style="border-radius: 6px;">
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-bold text-secondary" style="font-size: 13px;">Note Tecniche / Specifiche</label>
                            <textarea name="note" class="form-control" rows="3" placeholder="Note aggiuntive..." style="border-radius: 6px; resize: none;"></textarea>
                        </div>
                    </div>
                    
                    <div class="modal-footer" style="background-color: #f8f9fa; border-top: 1px solid #eeeeee; padding: 12px 20px;">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" style="border: none; border-radius: 6px; font-weight: 600; padding: 6px 18px;">Annulla</button>
                        <button type="submit" class="btn btn-primary" style="background-color: #0d6efd; border: none; border-radius: 6px; font-weight: 600; padding: 6px 18px;">Salva Componente</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <% if (apriModificaSubito) { %>
    <div class="modal fade" id="modalModificaComponente" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content" style="border-radius: 12px; overflow: hidden; border: none; box-shadow: 0 10px 30px rgba(0,0,0,0.3);">
                
                <div class="modal-header text-white" style="background-color: #2b2e33; padding: 15px 20px;">
                    <h5 class="modal-title" style="font-size: 16px; font-weight: 600; display: flex; align-items: center;">
                        <i class="bi bi-pencil-square text-warning me-2" style="font-size: 1.2rem;"></i> Scheda Tecnica: <%= compInModifica.getNome() %>
                    </h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" onclick="window.location.href='GestioneComponentiServlet';" aria-label="Close"></button>
                </div>
                
                <form action="GestioneComponentiServlet" method="POST">
                    <input type="hidden" name="azione" value="aggiorna">
                    <input type="hidden" name="id" value="<%= compInModifica.getId_componente() %>">
                    <input type="hidden" name="from" value="${provenienza}">
                    
                    <div class="modal-body" style="padding: 20px; background-color: #ffffff;">
                        
                        <div class="mb-3">
                            <label class="form-label fw-bold text-secondary" style="font-size: 13px;">Nome Componente</label>
                            <input type="text" name="nome" value="<%= compInModifica.getNome() %>" class="form-control" style="border-radius: 6px;" required>
                        </div>
                        
                        <div class="mb-3">
                            <label class="form-label fw-bold text-secondary" style="font-size: 13px;">Prezzo d'acquisto (€)</label>
                            <input type="number" step="0.01" name="prezzo" value="<%= compInModifica.getPrezzo() %>" class="form-control" style="border-radius: 6px;">
                        </div>
                        
                        <div class="mb-3">
                            <label class="form-label fw-bold text-secondary" style="font-size: 13px;">Data Acquisto</label>
                            <input type="date" name="data_acquisto" value="<%= compInModifica.getData_acquisto() %>" class="form-control" style="border-radius: 6px;">
                        </div>
                        
                        <div class="mb-3">
                            <label class="form-label fw-bold text-secondary" style="font-size: 13px;">Manutenzione ogni (giorni)</label>
                            <input type="number" name="manutenzione_ogni_giorni" value="<%= compInModifica.getManutenzione_ogni_giorni() %>" class="form-control" style="border-radius: 6px;">
                        </div>
                        
                        <div class="mb-3">
                            <label class="form-label fw-bold text-secondary" style="font-size: 13px;">Vita media stimata (mesi)</label>
                            <input type="number" name="vita_media_mesi" value="<%= compInModifica.getVita_media_mesi() %>" class="form-control" style="border-radius: 6px;">
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold text-secondary" style="font-size: 13px;">Data Ultima Manutenzione</label>
                            <input type="date" name="ultima_manutenzione" value="<%= compInModifica.getUltima_manutenzione() %>" class="form-control" style="border-radius: 6px;">
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold text-secondary" style="font-size: 13px;">Path Foto Scontrino/Garanzia</label>
                            <input type="text" name="foto_path" value="<%= (compInModifica.getFoto_scontrino_path() != null) ? compInModifica.getFoto_scontrino_path() : "" %>" placeholder="es. /img/scontrini/pompa.jpg" class="form-control" style="border-radius: 6px;">
                        </div>
                        
                        <div class="mb-3">
                            <label class="form-label fw-bold text-secondary" style="font-size: 13px;">Note e Dettagli Tecnici</label>
                            <textarea name="note" class="form-control" rows="3" style="border-radius: 6px; resize: none;"><%= (compInModifica.getNote() != null) ? compInModifica.getNote() : "" %></textarea>
                        </div>

                        <div class="p-3 mb-2 rounded border style="background-color: #fdf2f2; border-color: #fab1a0; display: flex; justify-content: space-around;">
                            <label class="fw-bold" style="cursor: pointer; color: #d63031; font-size: 13px; display: flex; align-items: center;">
                                <input type="checkbox" name="rotto" value="true" <%= (compInModifica.isRotto()) ? "checked" : "" %> style="margin-right: 6px; transform: scale(1.1);"> 🚨 SEGNA ROTTO
                            </label>
                            <label class="fw-bold" style="cursor: pointer; color: #e17055; font-size: 13px; display: flex; align-items: center;">
                                <input type="checkbox" name="difettoso" value="true" <%= (compInModifica.isDifettoso()) ? "checked" : "" %> style="margin-right: 6px; transform: scale(1.1);"> ⚠️ DIFETTOSO
                            </label>
                        </div>
                    </div>
                    
                    <div class="modal-footer" style="background-color: #f8f9fa; border-top: 1px solid #eeeeee; padding: 12px 20px;">
                        <button type="button" class="btn btn-secondary" onclick="window.location.href='GestioneComponentiServlet';" style="border: none; border-radius: 6px; font-weight: 600; padding: 6px 18px;">Annulla</button>
                        <button type="submit" class="btn btn-warning text-white" style="background-color: #f39c12; border: none; border-radius: 6px; font-weight: 600; padding: 6px 18px;">Aggiorna Componente</button>
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
            // 1. Inizializza e mostra la modale del componente
            var myModal = new bootstrap.Modal(document.getElementById('modalModificaComponente'), {
                backdrop: 'static',
                keyboard: false
            });
            myModal.show();

            // 2. Pulizia URL sicura al 100%
            // Usiamo un micro-ritardo di 50ms per evitare conflitti con l'animazione di Bootstrap
            setTimeout(function() {
                if (window.history.replaceState) {
                    // window.location.pathname prende in automatico l'URL attuale della servlet (es: /MioProgetto/ComponentiServlet)
                    // escludendo totalmente la parte "?azione=modificaForm&id=..."
                    window.history.replaceState(null, null, window.location.pathname);
                }
            }, 50);
        <% } %>
    });
	</script>
    
</body>
</html>