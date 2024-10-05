-- MySQL dump 10.13  Distrib 8.0.38, for Win64 (x86_64)
--
-- Host: localhost    Database: dbinfox
-- ------------------------------------------------------
-- Server version	8.0.39

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
-- Table structure for table `tbos`
--

DROP TABLE IF EXISTS `tbos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tbos` (
  `os` int NOT NULL AUTO_INCREMENT,
  `data_os` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `tipo` varchar(255) NOT NULL,
  `status_equipamento` varchar(255) NOT NULL,
  `nome_equipamento` varchar(255) NOT NULL,
  `marca` varchar(255) DEFAULT NULL,
  `modelo` varchar(255) DEFAULT NULL,
  `numero_serie` varchar(255) DEFAULT NULL,
  `descricao_problema` varchar(255) NOT NULL,
  `descricao_reparo` varchar(255) DEFAULT NULL,
  `nome_tecnico` varchar(255) NOT NULL,
  `valor` decimal(10,2) DEFAULT NULL,
  `idcliente` int NOT NULL,
  PRIMARY KEY (`os`),
  KEY `idcliente` (`idcliente`),
  CONSTRAINT `tbos_ibfk_1` FOREIGN KEY (`idcliente`) REFERENCES `tbclientes` (`idcliente`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tbos`
--

LOCK TABLES `tbos` WRITE;
/*!40000 ALTER TABLE `tbos` DISABLE KEYS */;
INSERT INTO `tbos` VALUES (3,'2024-10-04 21:30:52','Orçamento','Aguardando Análise.','Notebook','Dell','X15','123456789','Não liga','Troca da fonte','Antonio N',200.00,8),(4,'2024-10-04 21:35:53','OS','Equipamento Entregue.','Impressora','','','','Papel travando','','Antonio N',90.00,8),(5,'2024-10-04 23:04:57','Orçamento','Aguardando Análise.','PC','','','','Não liga','','Antonio N',0.00,8),(6,'2024-10-04 23:08:45','Orçamento','Aprovado pelo Cliente.','Impressora','','Dell','','Conexão internet','','Antonio N',0.00,8),(7,'2024-10-04 23:09:45','Orçamento','Aguardando Análise.','Notebook','Dell','X15','123456789','Não liga','Troca da fonte','Antonio N',200.00,8),(8,'2024-10-04 23:27:19','Orçamento','Aguardando Análise.','Tablet','','','','','','Antonio N',0.00,8),(9,'2024-10-05 03:54:50','OS','Reparo em Andamento.','Televisão','LG','XYZ','123456789','Problema no Led','Troca do Led','Antonio Nascimento',230.00,11),(10,'2024-10-05 04:00:41','Orçamento','Aguardando Análise.','Notebook Dell','','','','Travando','','Gabriel',0.00,11);
/*!40000 ALTER TABLE `tbos` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-10-05  2:16:18
