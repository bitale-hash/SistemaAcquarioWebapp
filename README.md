# Sistema Acquario Webapp 🐠

Applicazione Web Java per la gestione e il monitoraggio di un sistema acquaponico/acquario. Il progetto è sviluppato con struttura nativa Dynamic Web Project (Eclipse) ed è configurato per essere eseguito all'interno di **GitHub Codespaces** sfruttando l'estensione Apache Tomcat integrata e MySQL.

---

## 🚀 Come avviare il progetto su Codespaces 

1) vai su codespaces e apri un "blank" template

2) apri il terminale e clona il progetto
    - git clone https://github.com/bitale-hash/SistemaAcquarioWebapp

3)  Naviga dentro src -> main -> webapp -> WEB-INF.
    Fai click destro sulla cartella WEB-INF e seleziona New Folder (Nuova cartella).
    Nominala esattamente lib

4)  scarica e inserisci dentro quella cartella
    - mysql-connector-j-9.3.0.jar  
    - json-20231013.jar
    - jakarta.servlet.jsp.jstl-api-3.0.0.jar
    - jakarta.servlet.jsp.jstl-3.0.1.jar

5) Clicca sull'icona delle Estensioni nella barra laterale sinistra di  Codespaces (quella con i quattro quadratini).

    -Cerca e installa Extension Pack for Java (da Microsoft).

    -Cerca e installa Community Server Connectors (o in alternativa Tomcat for Java).

6)Dopo che hai installato le estensioni, dovresti avere una nuova sezione dedicata ai server nella barra laterale sinistra di Codespaces, cerca un'icona a forma di server o una nuova scheda chiamata Servers (o Tomcat). 

    6.1) Clicca sul tasto + (Add Server) che trovi in quella sezione.
    Oppure tasto destro e clicca su (Create new server)

    6.2)Se ti chiede quale server aggiungere, scegli Apache Tomcat.

    6.3) Ti chiederà la versione: seleziona Tomcat 10.1. 

7)Nella sezione Servers dove ora vedi Tomcat, fai click destro sopra il nome del server apache-tomcat 10.1.

    7.1)Seleziona l'opzione Add Web App (oppure add Deployment).

    7.2)Ti si aprirà una barra di ricerca in alto: seleziona la cartella principale del tuo progetto (SistemaAcquarioWebapp/src/main/webapp).

    7.3)Una volta fatto questo, fai nuovamente click destro su  apache-tomcat 10.1 e seleziona Start per avviare il server.

    7.4) Adesso vai su PORTE di fianco al TERMINALE e apri la pagina nel browser. Assicurati che il link finisca con /login.jsp

    7.5) se non funziona (errore 404) 
        -->In alto, apri la palette dei comandi premendo F1 (oppure Ctrl + Shift + P su Windows / Cmd + Shift + P su Mac).

        Scrivi nella barra che sorge in alto: Java: Clean Java Language Server Workspace e premi Invio.

        Se ti chiede di ricaricare la finestra (Restart), conferma.

8) se persiste l errore 404, il progetto non è ancora stato compilato da VS Code . Scrivi nel terminale:
    -  mkdir -p src/main/webapp/WEB-INF/classes
    -  javac -cp "/usr/local/tomcat/lib/*" -d src/main/webapp/WEB-INF/classes src/main/java/it/acquario/  controller/*.java src/main/java/it/acquario/model/*.java src/main/java/it/acquario/util/*.java 2>/dev/null || javac -cp "/usr/local/tomcat/lib/*" -d src/main/webapp/WEB-INF/classes src/main/java/it/acquario/controller/*.java
    -  invia tutto il messaggio a ad un AI per farlo controllare

    


9) nel terminale scrivi
        -  sudo apt-get update && sudo apt-get install -y mysql-server
        -  sudo service mysql start
        -  sudo mysql -u root -e "CREATE DATABASE IF NOT EXISTS db_acquario;"
        -  sudo mysql -u root db_acquario < db_acquario.sql











# Sistema Acquario Webapp 🐠

Applicazione Web Java per la gestione e il monitoraggio di un sistema acquaponico/acquario. Il progetto nasce come struttura nativa *Dynamic Web Project* (Eclipse) ed è ottimizzato per essere eseguito all'interno di **GitHub Codespaces** sfruttando l'estensione Apache Tomcat integrata e un’istanza locale di MySQL.

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

