# Sistema Acquario Webapp 🐠🌱

E' un'applicazione web enterprise per il monitoraggio domotico, chimico e biologico di un ecosistema ibrido composto da un **Acquario** e una **Serra idroponica/acquaponica**. 

L'applicazione simula, riceve ed elabora la telemetria proveniente da moduli hardware esterni basati su microcontrollore **ESP32**, centralizzando i dati all'interno di una dashboard interattiva. Il sistema permette agli operatori e agli amministratori di monitorare i parametri vitali dell'ambiente, configurare le soglie di automazione dell'hardware e gestire i cicli di manutenzione preventiva dei componenti fisici per azzerare i tassi di mortalità della fauna biologica.

---

## 🛠️ Tech Stack & Architettura (Tecnologie Utilizzate)

Il progetto è sviluppato seguendo l'architettura **MVC (Model-View-Controller)** standard per le applicazioni web Java in ambiente enterprise:

* **Backend (Core Logico):** * **Java EE / Jakarta EE:** Utilizzato per lo sviluppo della logica di business.
    * **Java Servlets (Servlet API):** Gestione del routing delle richieste HTTP, elaborazione dei dati di login, controllo dei componenti e smistamento della telemetria.
    * **JDBC (Java Database Connectivity):** Interfaccia nativa per la comunicazione efficiente e sicura tra l'applicazione Java e lo strato di persistenza dati.
* **Database (Persistenza):**
    * **MySQL:** Database relazionale utilizzato per la persistenza di utenti, log ambientali ed energetici, storico dei messaggi e stato dei componenti fisici.
* **Frontend (Interfaccia Utente):**
    * **JSP (JavaServer Pages) & JSTL:** Generazione dinamica delle pagine lato server.
    * **HTML5 & CSS3:** Struttura e layout responsivo e moderno della dashboard.
    * **JavaScript:** Gestione dei grafici interattivi (andamento pH/Temperature), aggiornamenti dinamici dell'interfaccia e interazioni della console RAW.
* **Formato Scambio Dati:**
    * **JSON (org.json):** Standard utilizzato per l'esegesi, il parsing e la diagnostica dei payload telematici simulati dall'hardware ESP32.
* **Ambiente di Server & Deployment:**
    * **Apache Tomcat 10.1:** Web server e servlet container per l'esecuzione dell'applicazione.
    * **GitHub Codespaces:** Ambiente di sviluppo containerizzato basato su Linux Cloud.

---

## 📺 Panoramica delle Funzionalità (Cosa fa l'App)

L'interfaccia si articola in quattro moduli operativi principali:

### 1. Monitoraggio Analitico & Grafici 📊
* **Telemetria Energetica:** Visualizzazione in tempo reale dello stato di carica della batteria alimentata da pannelli solari dedicati all'impianto.
* **Analisi Chimica e Climatica:** Grafici cartesiani temporali interattivi per il tracciamento simultaneo di **Temperatura dell'acqua (°C)** e **Livelli di pH**, prevenendo anomalie biologiche.
* **Flusso Idrico e Luminoso:** Istogrammi avanzati sul consumo d'acqua (Litri) per l'irrigazione della serra e sulla corrente prodotta in mA dal pannello fotovoltaico.
* **Notifiche Allarmi:** Centro notifiche centralizzato per l'identificazione immediata di guasti hardware (es. malfunzionamento plafoniere LED).

### 2. Gestione Ecosistema & Componenti 🐟⚙️
* **Manutenzione Preventiva Hardware:** Tabella di monitoraggio dello stato di usura dei dispositivi fisici (Bombola CO2, Skimmer, Pompe di ricircolo, Elettrodi). Il sistema calcola i giorni rimanenti alla manutenzione segnalando i componenti *VECCHI*, *DIFETTOSI* o con *SCADENZA SUPERATA*.
* **Anagrafe e Controllo Biologico:** Registro per specie ittica (es. Scalare, Orata) che monitora range ideali di sopravvivenza, alimentazione consigliata, densità massima e indicatori comportamentali di stress.

### 3. Pannello di Controllo Domotico (Configurazione ESP32) ⚙️📡
Interfaccia di amministrazione remota per sovrascrivere le soglie logiche del microcontrollore:
* **Parametri Vasca:** Setpoint minimi e massimi di sicurezza per pH, riscaldatori e temporizzazione del distributore automatico di cibo.
* **Automazione Serra:** Configurazione della durata dell'irrigazione (giorno/notte) e trigger intelligenti basati sul surplus di energia solare prodotta.

### 4. Registro Diagnostico & Messaggi RAW 📜🛠️
* **Console Log Avanzata:** Tracciamento di ogni evento di sistema generato dall'interfaccia utente o dai sensori.
* **Payload Inspector:** Ispezione visiva dei file JSON nativi scambiati con i dispositivi fisici, utile ai fini di debug dell'infrastruttura di rete hardware.

---

## 🚀 Come avviare il progetto su Codespaces

Segui questi passaggi nell'ordine esatto per configurare l'ambiente, il database e avviare l'applicazione.

1. Clona il progetto e prepara le librerie
Apri il terminale del tuo Codespace ed esegui i seguenti comandi per scaricare il progetto e creare la struttura delle cartelle:

```bash
# Se parti da un template blank, clona il progetto (altrimenti salta questo comando)
git clone [https://github.com/bitale-hash/SistemaAcquarioWebapp](https://github.com/bitale-hash/SistemaAcquarioWebapp)
cd SistemaAcquarioWebapp

# Crea le cartelle necessarie per le librerie e le classi compilate
mkdir -p src/main/webapp/WEB-INF/lib
mkdir -p src/main/webapp/WEB-INF/classes
mkdir -p WEB-INF/classes

📌 Nota Librerie (.jar): Assicurati che dentro la cartella src/main/webapp/WEB-INF/lib/ siano presenti i seguenti file (indispensabili per il funzionamento):

-  mysql-connector-j-9.3.0.jar (o versione compatibile)
-  json-20231013.jar
-  jakarta.servlet.jsp.jstl-api-3.0.0.jar
-  jakarta.servlet.jsp.jstl-3.0.1.jar

2. Configura e avvia il Database MySQL
Codespaces non ha MySQL attivo di default. Configuralo e importa i dati eseguendo questi comandi in sequenza nel terminale:

# 1. Installa il server MySQL
sudo apt-get update && sudo apt-get install -y mysql-server

# 2. Avvia il servizio MySQL
sudo service mysql start

# 3. Crea il database e importa le tabelle dal file .sql
sudo mysql -u root -e "CREATE DATABASE IF NOT EXISTS db_acquario;"
sudo mysql -u root db_acquario < db_acquario.sql

# 4. Imposta la password di root a '1234' (richiesta da DBConnessione.java)
sudo mysql -u root -e "ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '1234'; FLUSH PRIVILEGES;"

3. Installa le Estensioni richieste
Spostati nella barra laterale sinistra di VS Code (icona dei quattro quadratini) e installa:

-  Extension Pack for Java (Microsoft)
-  Community Server Connectors (per la gestione di Tomcat)

4. Compila il progetto 🛠️
Trattandosi di un Dynamic Web Project senza Maven, compiliamo manualmente tutte le classi Java includendo le dipendenze corrette di Jakarta e legandole a Tomcat. Esegui questi comandi nel terminale :

cd SistemaAcquarioWebapp

# Trova l'API servlet ed esegui la compilazione totale
JAR_PATH=$(find /home/codespace/.vscode-remote/extensions/ -name "jakarta.servlet-api_6.1.0.jar" | head -n 1)

javac -cp "$JAR_PATH:src/main/webapp/WEB-INF/lib/*" -d src/main/webapp/WEB-INF/classes src/main/java/it/acquario/model/*.java src/main/java/it/acquario/util/*.java src/main/java/it/acquario/dao/*.java src/main/java/it/acquario/controller/*.java

# Allinea le classi compilate per il deployment di Tomcat
cp -r src/main/webapp/WEB-INF/classes/* WEB-INF/classes/ 2>/dev/null

5. Configura Apache Tomcat e Avvia
Nella barra laterale sinistra, vai sulla scheda SERVERS.

Clicca sul tasto + (Add Server) e seleziona Apache Tomcat (versione 10.1).

Fai click destro su apache-tomcat-10.1.0 appena aggiunto e seleziona Add Web App (o Deploy Folder).

Scegli la cartella principale del progetto: SistemaAcquarioWebapp.

Fai nuovamente click destro sul server Tomcat e seleziona Start (o Restart).

Spostati sulla scheda PORTS (di fianco al terminale in basso), individua la porta 8080 e clicca sull'icona del mondo per aprire l'applicazione nel browser.

Modifica l'URL finale per puntare direttamente alla pagina di login:
https://<tuo-url-codespace>-8080.app.github.dev/SistemaAcquarioWebapp/login.jsp

Credenziali di Test:
Username: admin

Password: 1234


🛠️ Risoluzione dei Problemi (Troubleshooting)
Errore 404 all'avvio o dopo il login? Significa che le modifiche ai file Java non sono state registrate o Tomcat ha perso il puntamento delle classi. Riesegui il blocco di comandi del Punto 4 (Compila il progetto) e fai Restart del server Tomcat dal pannello Servers.

Errore di credenziali errate persistente?
Verifica che il database sia popolato lanciando nel terminale: sudo mysql -u root db_acquario -e "SELECT * FROM utente;"

