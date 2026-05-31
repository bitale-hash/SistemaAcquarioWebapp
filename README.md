# Sistema Acquario Webapp 🐠

Applicazione Web Java per la gestione e il monitoraggio di un sistema acquaponico/acquario. Il progetto è sviluppato con struttura nativa Dynamic Web Project (Eclipse) ed è configurato per essere eseguito all'interno di **GitHub Codespaces** sfruttando l'estensione Apache Tomcat integrata e MySQL.

---

## 🚀 Come avviare il progetto su Codespaces (Ogni volta che lo riapri)

Se hai appena riaperto il tuo GitHub Codespaces, i servizi saranno spenti. Copia e incolla questo blocco unico nel terminale per **accendere il Database e avviare il Server Tomcat** all'istante:

```bash
# 1. Avvia il database MySQL
sudo service mysql start

# 2. Avvia il server Apache Tomcat
/home/codespace/.rsp/redhat-community-server-connector/runtimes/installations/tomcat-10.1.0/apache-tomcat-10.1.0/bin/startup.sh

