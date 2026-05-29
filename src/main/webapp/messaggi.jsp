<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // Recupero i messaggi
    String msgS = (String) request.getAttribute("successo");
    String msgE = (String) request.getAttribute("errore");
    if (msgS == null) msgS = (String) session.getAttribute("successo");
    if (msgE == null) msgE = (String) session.getAttribute("errore");

    // Genero l'ID unico
    long msgId = System.currentTimeMillis();

    // Pulisco la sessione subito
    if (msgS != null) session.removeAttribute("successo");
    if (msgE != null) session.removeAttribute("errore");

    // Mostro il modal solo se c'è un messaggio
    if (msgS != null || msgE != null) {
%>
<style>
    .modal-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); display: flex; justify-content: center; align-items: center; z-index: 10000; }
    .modal-box { background: white; padding: 25px; border-radius: 8px; min-width: 320px; text-align: center; box-shadow: 0 10px 25px rgba(0,0,0,0.2); font-family: sans-serif; }
    .icon { font-size: 2.5em; margin-bottom: 15px; display: block; }
    .success-color { color: #2ecc71; }
    .error-color { color: #e74c3c; }
    .btn-ok { background-color: #3498db; color: white; border: none; padding: 10px 40px; border-radius: 5px; cursor: pointer; font-size: 1.1em; }
</style>

<div id="customModal" class="modal-overlay">
    <div class="modal-box">
        <span class="icon <%= (msgS != null) ? "success-color" : "error-color" %>">
            <%= (msgS != null) ? "✅" : "⚠️" %>
        </span>
        <div class="modal-content" style="margin-bottom:25px;">
            <%= (msgS != null) ? msgS : msgE %>
        </div>
        <button class="btn-ok" onclick="chiudiModal()">OK</button>
    </div>
</div>

<script>
    (function() {
        const currentMsgId = "<%= msgId %>";
        const modal = document.getElementById('customModal');

        window.chiudiModal = function() {
            if (modal) {
                modal.style.display = 'none';
                localStorage.setItem('ultimo_msg_chiuso', currentMsgId);
            }
        };

        // Verifichiamo se siamo tornati indietro
        const entries = window.performance.getEntriesByType("navigation");
        const isBackForward = entries.length > 0 && entries[0].type === "back_forward";
        
        // Se l'utente è tornato indietro, NON vogliamo vedere il vecchio messaggio
        if (isBackForward) {
            if (modal) modal.style.display = 'none';
            return;
        }

        // Se è un caricamento normale, mostriamo il messaggio solo se non è già stato chiuso
        const ultimoChiuso = localStorage.getItem('ultimo_msg_chiuso');
        if (ultimoChiuso === currentMsgId) {
            if (modal) modal.style.display = 'none';
        }
    })();
</script>
<% } %>