-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: db_acquario
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `componenti_fisici`
--

DROP TABLE IF EXISTS `componenti_fisici`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `componenti_fisici` (
  `id_componente` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) NOT NULL,
  `prezzo` decimal(10,2) DEFAULT '0.00',
  `data_acquisto` date NOT NULL,
  `manutenzione_ogni_giorni` int NOT NULL,
  `vita_media_mesi` int DEFAULT NULL,
  `foto_scontrino_path` varchar(255) DEFAULT NULL,
  `note` text,
  `ultima_manutenzione` date DEFAULT NULL,
  `rotto` tinyint(1) DEFAULT '0',
  `difettoso` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id_componente`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `componenti_fisici`
--

LOCK TABLES `componenti_fisici` WRITE;
/*!40000 ALTER TABLE `componenti_fisici` DISABLE KEYS */;
INSERT INTO `componenti_fisici` VALUES (5,'Bombola CO2 2Kg',15.00,'2025-11-05',30,48,NULL,'Ricarica bombola e controllo guarnizioni','2026-05-20',0,0),(6,'Skimmer Eheim 350',35.00,'2025-05-05',30,24,NULL,'Pulizia spugna interna','2026-05-18',0,0),(7,'Cannolicchi Ceramici Pro',25.50,'2023-05-05',120,24,NULL,'Sostituire parzialmente per evitare crolli batterici','2026-05-13',0,1),(8,'Kit Ventole Refrigeranti',40.00,'2025-07-05',150,36,NULL,'Controllare cuscinetti e polvere','2026-05-05',0,0),(13,'Pompa Ricircolo',0.00,'2026-05-07',30,24,NULL,'abracadrabra  abracadrabra','2026-05-13',0,0),(15,'Pompa Ricircolo',0.00,'2026-05-07',30,24,NULL,'f','2026-05-20',0,0),(16,'Riscaldatore Vintageef ',111.00,'2026-05-07',30,24,NULL,'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa','2026-05-13',0,1),(18,'Plafoniera LED Reef',450.00,'2025-01-10',90,60,NULL,'Luce spettro completo per coralli','2026-05-07',0,0),(19,'Pompa di Risalita 3000L/h',120.50,'2024-05-20',60,48,NULL,'Pulire la girante regolarmente','2026-05-08',0,0),(20,'Schiumatoio di Proteine',210.00,'2024-08-15',15,72,NULL,'Svuotare il bicchiere ogni settimana','2026-05-01',0,0),(21,'Riscaldatore Titanio 300W',45.00,'2023-10-01',180,24,NULL,'Controllare calibrazione termostato','2026-05-07',0,0),(22,'Sensore Densità Digitale',85.00,'2025-03-12',30,36,NULL,'Calibrare con soluzione 35ppt','2026-05-05',0,0),(23,'Filtro a letto fluido',75.00,'2022-01-15',45,48,NULL,'Sostituire resine periodicamente','2026-05-07',1,0),(24,'Pompa Movimento Sinistra',65.00,'2023-06-21',30,36,NULL,'Modello a flusso variabile','2026-05-13',0,0),(25,'Elettrodo pH',35.00,'2024-02-10',30,12,NULL,'Sonda consumabile, da sostituire spesso','2026-05-07',0,0),(26,'Dosometrica Calcio',110.00,'2025-04-01',60,60,NULL,'Testare precisione millimetrica','2026-05-07',0,0),(27,'Refrigeratore Teco TK500',680.00,'2023-05-05',365,120,NULL,'Pulizia filtri aria annuale','2026-05-07',0,0),(29,'Filtro Esterno Nuovo abra',11.00,'2025-01-08',30,24,NULL,'','2026-05-12',0,0),(31,'Plafoniera LED RGBW',100.00,'2026-05-07',30,24,NULL,'','2026-05-18',0,0);
/*!40000 ALTER TABLE `componenti_fisici` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `configurazione`
--

DROP TABLE IF EXISTS `configurazione`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `configurazione` (
  `id_config` int NOT NULL AUTO_INCREMENT,
  `parametro` varchar(50) NOT NULL,
  `valore` varchar(100) NOT NULL,
  `tipo_dato` enum('int','float','string','boolean','datetime') NOT NULL,
  `categoria` enum('ACQUARIO','SERRA','SISTEMA','TIME') NOT NULL,
  `descrizione` varchar(255) DEFAULT NULL,
  `ultima_modifica` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `minimo` float DEFAULT '0',
  `massimo` float DEFAULT '2000',
  PRIMARY KEY (`id_config`),
  UNIQUE KEY `parametro` (`parametro`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `configurazione`
--

LOCK TABLES `configurazione` WRITE;
/*!40000 ALTER TABLE `configurazione` DISABLE KEYS */;
INSERT INTO `configurazione` VALUES (1,'ph_min','6.5','float','ACQUARIO','Soglia minima pH acqua per allarme','2026-05-19 08:04:46',5,7),(2,'ph_max','8.0','float','ACQUARIO','Soglia massima pH acqua per allarme','2026-05-19 08:12:56',7,9),(3,'temp_min','13.0','float','ACQUARIO','Soglia minimatemperatura acqua (°C)','2026-05-19 08:04:46',0,2000),(4,'temp_max','18.0','float','ACQUARIO','Soglia massima temperatura acqua (°C)','2026-05-19 08:12:56',0,2000),(7,'durata_ciclo_minuti','15.0','int','SERRA','Quanto dura l irrigazione (minuti)','2026-05-18 12:29:58',0,2000),(14,'pasti_giornalieri','12.0','int','ACQUARIO','Ogni quante ore viene erogato il cibo ai pesci','2026-05-18 12:29:58',0,2000),(21,'freq_controlli_frequenti_min','5.0','int','SISTEMA','Controllo sensori critici (Pioggia/...)  (minuti)','2026-05-13 15:04:29',0,2000),(22,'freq_controlli_giornalieri_ore','24.0','int','SISTEMA','Controllo sensori sistema (Pompa/Batteria/...) (ore)','2026-05-13 15:04:29',0,2000),(23,'freq_controlli_settimanali_ore','30.0','int','SISTEMA','Checklist manutenzione filtri ogni tot. (giorni)','2026-05-18 12:29:58',0,2000),(24,'freq_controlli_mensili_ore','90.0','int','SISTEMA','Checklist pulizia profonda e calibrazione (giorni)','2026-05-18 12:29:58',0,2000),(25,'ultimo_controllo_frequente','2026-05-08 00:00:00','string','TIME','Ultima rilevazione pioggia/emergenze','2026-05-13 14:05:46',0,2000),(26,'ultimo_controllo_giornaliero','2026-05-08 00:00:00','string','TIME','Ultimo check-up completo parametri giornalieri','2026-05-13 14:05:46',0,2000),(27,'ultimo_controllo_settimanale','2026-05-08 00:00:00','string','TIME','Ultimo check-up completo parametri settimanali','2026-05-13 14:05:46',0,2000),(28,'ultimo_controllo_mensile','2026-05-08 00:00:00','string','TIME','Ultimo check-up completo parametri mensili','2026-05-13 14:05:46',0,2000),(29,'ultima_irrigazione','2026-05-08 00:00:00','string','TIME','Ultimo irrigazione','2026-05-13 14:05:46',0,2000),(30,'ultimo_pasto_pesci','2026-05-08 00:00:00','string','TIME','Ultima volta che è stato attivato l\'erogatore','2026-05-13 14:05:46',0,2000),(31,'litri_vasca','1000.0','int','ACQUARIO','Capacità totale dell\'acquario in litri','2026-05-13 15:04:29',0,2000),(36,'intervallo_cicli_irrigazione_giorno','15','int','SERRA','Ogni quanto si ripetere l irrigazione durante il giorno (minuti)','2026-05-18 15:30:00',0,2000),(37,'intervallo_cicli_irrigazione_notte','40','int','SERRA','Ogni quanto si ripetere l irrigazione durante il notte(minuti)','2026-05-18 15:30:35',0,2000),(38,'precisione_durata_ciclo_irrigazione','2','int','SERRA','Di quanti minuti può variare la durata dell\'irrigazione','2026-05-18 15:33:34',0,2000),(39,'mA_per_irrigazione_extra','1000','int','SERRA','Attivare irrigazione extra quando l\'energia prodotta dal pannello solare è maggiore di (mA)','2026-05-18 15:39:34',0,2000),(40,'mA_per_irrigazione_minore','300','int','SERRA','Diminuire l\'irrigazione quando l\'energia prodotta dal pannello solare è minore di (mA) (solo giorno)','2026-05-18 15:44:47',0,2000);
/*!40000 ALTER TABLE `configurazione` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `log_ambientale`
--

DROP TABLE IF EXISTS `log_ambientale`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `log_ambientale` (
  `id_log` int NOT NULL AUTO_INCREMENT,
  `data_ora` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `temperatura_vasca` decimal(4,2) DEFAULT NULL,
  `ph_vasca` decimal(3,1) DEFAULT NULL,
  `livello_acqua_vasca_cm` int DEFAULT NULL,
  `acqua_piante_ml` int DEFAULT NULL,
  `umidita_terreno` int DEFAULT NULL,
  `temperatura_aria` decimal(4,2) DEFAULT NULL,
  `umidita_aria` int DEFAULT NULL,
  `luminosita_lux` int DEFAULT NULL,
  PRIMARY KEY (`id_log`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `log_ambientale`
--

LOCK TABLES `log_ambientale` WRITE;
/*!40000 ALTER TABLE `log_ambientale` DISABLE KEYS */;
INSERT INTO `log_ambientale` VALUES (1,'2026-05-20 06:00:00',21.50,6.8,95,0,60,19.00,70,800),(2,'2026-05-20 08:30:00',22.80,6.7,90,250,55,22.50,65,35000),(3,'2026-05-20 11:15:00',24.20,6.6,82,0,75,26.00,50,65000),(4,'2026-05-20 14:00:00',24.00,6.6,75,500,70,25.50,55,42000),(5,'2026-05-20 16:45:00',23.10,6.7,60,0,65,21.00,68,12000),(6,'2026-05-20 19:30:00',22.00,6.8,50,0,62,18.50,75,0),(7,'2026-05-20 23:00:00',21.00,6.9,45,0,60,16.00,80,0);
/*!40000 ALTER TABLE `log_ambientale` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `log_energia`
--

DROP TABLE IF EXISTS `log_energia`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `log_energia` (
  `id_log` int NOT NULL AUTO_INCREMENT,
  `data_ora` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `livello_batteria` int DEFAULT NULL,
  `tensione_volt` float DEFAULT NULL,
  `corrente_prodotta_ma` float DEFAULT NULL,
  `corrente_consumata_ma` float DEFAULT NULL,
  `errore_rilevato` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id_log`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `log_energia`
--

LOCK TABLES `log_energia` WRITE;
/*!40000 ALTER TABLE `log_energia` DISABLE KEYS */;
INSERT INTO `log_energia` VALUES (1,'2026-05-20 06:00:00',70,12.5,450,350,NULL),(2,'2026-05-20 08:30:00',75,12.8,1200,380,NULL),(3,'2026-05-20 11:15:00',85,13.4,2350,400,NULL),(4,'2026-05-20 14:00:00',90,13.6,1900,410,NULL),(5,'2026-05-20 16:45:00',88,13.2,700,420,NULL),(6,'2026-05-20 19:30:00',80,12.6,0,450,NULL),(7,'2026-05-20 23:00:00',72,12.3,0,320,NULL);
/*!40000 ALTER TABLE `log_energia` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notifiche_allarmi`
--

DROP TABLE IF EXISTS `notifiche_allarmi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notifiche_allarmi` (
  `id_allarme` int NOT NULL AUTO_INCREMENT,
  `livello` enum('INFO','WARNING','CRITICAL') NOT NULL,
  `messaggio` text NOT NULL,
  `letta` tinyint(1) NOT NULL DEFAULT '0',
  `data_ora` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `id_componente_rif` int DEFAULT NULL,
  `risolto` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id_allarme`),
  KEY `fk_allarme_componente` (`id_componente_rif`),
  CONSTRAINT `fk_allarme_componente` FOREIGN KEY (`id_componente_rif`) REFERENCES `componenti_fisici` (`id_componente`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifiche_allarmi`
--

LOCK TABLES `notifiche_allarmi` WRITE;
/*!40000 ALTER TABLE `notifiche_allarmi` DISABLE KEYS */;
INSERT INTO `notifiche_allarmi` VALUES (1,'CRITICAL','Problema risolto manualmente dall\'utente',1,'2026-05-18 15:01:05',5,1),(2,'CRITICAL','Allarme: il componente \'Plafoniera LED RGBW\' risulta guasto o rotto.',1,'2026-05-18 10:07:31',NULL,1),(3,'INFO','Filtro meccanico sostituito con successo.',0,'2026-05-05 16:00:00',6,0),(4,'CRITICAL','Temperatura critica: 31.5°C rilevata nell\'acquario principale.',0,'2026-05-04 06:30:00',7,0),(5,'CRITICAL','ph critico: 8.7',0,'2026-05-02 10:15:00',16,0),(6,'CRITICAL','ph critico: 5.7',0,'2026-05-01 16:00:00',16,0),(9,'CRITICAL','Problema risolto manualmente dall\'utente',1,'2026-05-18 09:33:58',8,1),(11,'CRITICAL','Errore critico: Pompa di risalita bloccata! Portata acqua nulla.',0,'2026-05-08 08:28:58',8,1),(12,'WARNING','Attenzione: Sensore di rabbocco attivato da più di 5 minuti.',0,'2026-05-08 08:28:58',6,1),(14,'CRITICAL','Componente #21: Rilevata temperatura anomala (35°C). Spegnimento di sicurezza attivato.',1,'2026-05-08 08:32:59',21,1),(15,'WARNING','Componente #21: Ore di esercizio limite raggiunte. Verificare lo stato dei filtri.',1,'2026-05-08 08:32:59',21,1),(16,'INFO','Componente #21: Ricalibrazione automatica dei sensori completata.',1,'2026-05-08 08:32:59',21,1),(17,'CRITICAL','Componente #21: Perdita di segnale rilevata. Controllare alimentazione e connessione BUS.',0,'2026-05-08 08:32:59',21,1),(18,'INFO','Componente #21: Aggiornamento firmware v2.4 installato con successo.',0,'2026-05-01 12:20:00',21,1),(19,'WARNING','Componente #21: Rilevato flusso d\'acqua intermittente negli ultimi 10 minuti.',1,'2026-05-08 08:32:59',21,1),(22,'CRITICAL','Allarme: il componente \'Plafoniera LED RGBW\' risulta guasto o rotto.',1,'2026-05-18 10:07:31',NULL,1),(23,'CRITICAL','Allarme: il componente \'Plafoniera LED RGBW\' risulta guasto o rotto.',1,'2026-05-18 10:07:31',NULL,1),(25,'CRITICAL','Problema risolto manualmente dall\'utente',1,'2026-05-17 11:26:09',7,1),(26,'WARNING','Problema risolto manualmente dall\'utente',1,'2026-05-17 11:27:11',7,1),(27,'INFO','Stato Pompa: Regolare',1,'2026-05-15 14:44:44',6,0),(28,'CRITICAL','Problema risolto manualmente dall\'utente',1,'2026-05-17 13:30:51',5,1),(29,'CRITICAL','Allarme: il componente \'Plafoniera LED RGBW\' risulta guasto o rotto.',1,'2026-05-18 10:07:31',NULL,1),(30,'WARNING','Problema risolto manualmente dall\'utente',1,'2026-05-17 13:49:29',5,1),(31,'CRITICAL','Problema risolto manualmente dall\'utente',1,'2026-05-20 12:40:25',15,1),(32,'WARNING','Sostituzione cartuccia carbone attivo completata.',1,'2026-05-16 13:42:40',7,1);
/*!40000 ALTER TABLE `notifiche_allarmi` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pesci`
--

DROP TABLE IF EXISTS `pesci`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pesci` (
  `id_pesce` int NOT NULL AUTO_INCREMENT,
  `specie` varchar(100) NOT NULL,
  `tipo_cibo` varchar(100) DEFAULT NULL,
  `temp_min` decimal(4,2) NOT NULL,
  `temp_max` decimal(4,2) NOT NULL,
  `ph_min` decimal(4,2) DEFAULT NULL,
  `ph_max` decimal(4,2) DEFAULT NULL,
  `num_max_mc` int NOT NULL,
  `segnali_stress` text,
  PRIMARY KEY (`id_pesce`),
  UNIQUE KEY `specie` (`specie`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pesci`
--

LOCK TABLES `pesci` WRITE;
/*!40000 ALTER TABLE `pesci` DISABLE KEYS */;
INSERT INTO `pesci` VALUES (1,'Scalare','Chironomus',12.00,26.00,6.20,8.30,2,'Inappetenza, isolamento'),(4,'orata max','pane, becatini ',13.00,23.00,6.20,8.20,12,'pancia in su , fermi'),(5,'orata','Chironomus',12.00,22.10,6.50,8.80,3,''),(9,'abracadabra','Chironomus',10.00,18.00,6.00,8.00,1,'');
/*!40000 ALTER TABLE `pesci` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `storico_messaggi`
--

DROP TABLE IF EXISTS `storico_messaggi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `storico_messaggi` (
  `id_history` int NOT NULL AUTO_INCREMENT,
  `tipo_controllo` enum('FREQUENTE','GIORNALIERO','SETTIMANALE','MENSILE','EVENTO') NOT NULL,
  `esito` enum('OK','ALLARME','INFO') DEFAULT 'INFO',
  `origine` enum('SISTEMA','UTENTE') DEFAULT 'SISTEMA',
  `messaggio` varchar(1000) NOT NULL,
  `elaborato` tinyint(1) DEFAULT '0',
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_history`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `storico_messaggi`
--

LOCK TABLES `storico_messaggi` WRITE;
/*!40000 ALTER TABLE `storico_messaggi` DISABLE KEYS */;
INSERT INTO `storico_messaggi` VALUES (1,'FREQUENTE','OK','SISTEMA','{\"device\":\"ESP32-Vasca\",\"temp_vasca\":24.2,\"ph\":6.6,\"status\":\"IDLE\"}',1,'2026-05-20 07:00:00'),(2,'FREQUENTE','ALLARME','SISTEMA','{\"device\":\"ESP32-Serra\",\"error\":\"Livello acqua sotto soglia minima (45cm)\",\"azione\":\"Attivazione pompa di rabbocco automatico\"}',1,'2026-05-20 08:15:00'),(3,'GIORNALIERO','INFO','SISTEMA','{\"report\":\"Resoconto 24h\",\"energia_prodotta_tot_wh\":4200,\"stato_batteria\":\"Ottimale (90%)\"}',1,'2026-05-20 10:00:00'),(4,'EVENTO','INFO','UTENTE','Lancio manuale ciclo di irrigazione forzata della serra (Durata: 5 minuti).',1,'2026-05-20 12:30:00'),(5,'SETTIMANALE','OK','SISTEMA','{\"controllo\":\"Stato filtri acquario\",\"portata_pompa_l_h\":650,\"efficienza\":100}',1,'2026-05-20 14:00:00'),(6,'FREQUENTE','ALLARME','SISTEMA','{\"device\":\"ESP32-Vasca\",\"allarme\":\"pH fuori soglia biologica rilevato: 6.3\",\"soglia_min_pesci\":6.5,\"azione_richiesta\":\"Erogazione correttore tampone\"}',0,'2026-05-20 15:15:00'),(7,'EVENTO','INFO','UTENTE','Modifica parametri ottimali: Nuova temperatura massima impostata a 27.5°C.',0,'2026-05-20 15:30:00');
/*!40000 ALTER TABLE `storico_messaggi` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `utenti`
--

DROP TABLE IF EXISTS `utenti`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `utenti` (
  `id_utente` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `ruolo` enum('ADMIN','OPERATORE') NOT NULL DEFAULT 'OPERATORE',
  `email` varchar(100) NOT NULL,
  PRIMARY KEY (`id_utente`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `utenti`
--

LOCK TABLES `utenti` WRITE;
/*!40000 ALTER TABLE `utenti` DISABLE KEYS */;
INSERT INTO `utenti` VALUES (1,'admin','1234','ADMIN','bitontiale@gmail.com'),(2,'utente1','1234','OPERATORE','email_operatore@esempio.com'),(3,'admin2','1234','ADMIN','tua_email_reale@gmail.com');
/*!40000 ALTER TABLE `utenti` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `zone_coltivazione`
--

DROP TABLE IF EXISTS `zone_coltivazione`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `zone_coltivazione` (
  `id_zona` int NOT NULL AUTO_INCREMENT,
  `nome_zona` varchar(50) NOT NULL,
  `tipo_coltura` varchar(50) DEFAULT NULL,
  `durata_min` int NOT NULL DEFAULT '0',
  `frequenza_ore` int NOT NULL DEFAULT '1',
  `pin_valvola` int NOT NULL,
  PRIMARY KEY (`id_zona`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `zone_coltivazione`
--

LOCK TABLES `zone_coltivazione` WRITE;
/*!40000 ALTER TABLE `zone_coltivazione` DISABLE KEYS */;
INSERT INTO `zone_coltivazione` VALUES (1,'Vasca verdure','Pomodori Ciliegino',30,1,4),(2,'Vasca spezie','Basilico e Menta',20,1,5),(3,'Vasca insalate','Lattuga Romana',10,1,6);
/*!40000 ALTER TABLE `zone_coltivazione` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-29 16:20:44
