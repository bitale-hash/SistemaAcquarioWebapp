// Eseguito al caricamento della pagina
document.addEventListener("DOMContentLoaded", function() {
    const container = document.getElementById('chat-container');
    const launcher = document.getElementById('chat-launcher');
    
    // STATO INIZIALE BLINDATO: All'avvio la chat è CHIUSA, l'icona è VISIBILE
    container.style.display = 'none';
    launcher.style.display = 'block';
    
    // Carica la cronologia visiva all'interno della finestra (che è nascosta)
    caricaMessaggiDallaMemoria();
    
    let cronologia = JSON.parse(localStorage.getItem('acquaponica_chat_history')) || [];
    
    // Se la cronologia è vuota, evochiamo il messaggio di benvenuto in background
    if (cronologia.length === 0) {
        sendMessage("init_welcome_message", true);
    }
});

function toggleChat() {
    const container = document.getElementById('chat-container');
    const launcher = document.getElementById('chat-launcher');
    
    // Inversione degli stati display
    if (container.style.display === 'none' || container.style.display === '') {
        container.style.display = 'block'; 
        launcher.style.display = 'none';
        document.getElementById('userInput').focus();
    } else {
        container.style.display = 'none'; 
        launcher.style.display = 'block';
    }
}

function sendMessage(text, isSystemCommand = false) {
    if(!text || text.trim() === "") return;
	
    const paroleDaIgnorare = ["home_", "stato_", "energia_", "grazie_","init_welcome_message","ciao", "buongiorno", "salve"];
    const deveEssereSalvato = !paroleDaIgnorare.includes(text.toLowerCase()) && !isSystemCommand;
    const chat = document.getElementById('chat-window');

    if(!isSystemCommand) {
        renderizzaMessaggio(text, 'user');
        if (deveEssereSalvato) {
            salvaInLocalStorage(text, 'user');
        }    
    }

    document.getElementById('userInput').value = '';
    chat.scrollTop = chat.scrollHeight;
	
    const cronologia = localStorage.getItem('acquaponica_chat_history') || "[]";
	
    fetch('DashboardChatbotServlet', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: 'message=' + encodeURIComponent(text) + '&history=' + encodeURIComponent(cronologia)
    })
    .then(response => response.text())
    .then(data => {
        renderizzaMessaggio(data, 'bot');
        if (deveEssereSalvato) {
            salvaInLocalStorage(data, 'bot');
        }
        chat.scrollTop = chat.scrollHeight;
    });
}

function renderizzaMessaggio(testo, autore) {
    const chat = document.getElementById('chat-window');
    const classe = (autore === 'user') ? 'user-bubble' : 'bot-bubble';
    chat.innerHTML += `<div class="${classe}">${testo}</div>`;
}

function salvaInLocalStorage(testo, autore) {
    let cronologia = JSON.parse(localStorage.getItem('acquaponica_chat_history')) || [];
    cronologia.push({ testo: testo, autore: autore });
    if(cronologia.length > 20) cronologia.shift();
    localStorage.setItem('acquaponica_chat_history', JSON.stringify(cronologia));
}

function caricaMessaggiDallaMemoria() {
    const chat = document.getElementById('chat-window');
    let cronologia = JSON.parse(localStorage.getItem('acquaponica_chat_history')) || [];
    chat.innerHTML = '';
    cronologia.forEach(msg => {
        renderizzaMessaggio(msg.testo, msg.autore);
    });
    chat.scrollTop = chat.scrollHeight;
}