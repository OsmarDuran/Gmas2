CREATE DATABASE  IF NOT EXISTS `gmas2` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `gmas2`;
-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: gmas2
-- ------------------------------------------------------
-- Server version	9.4.0

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
-- Table structure for table `asignacion`
--

DROP TABLE IF EXISTS `asignacion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `asignacion` (
  `id_asignacion` int NOT NULL AUTO_INCREMENT,
  `id_equipo` int NOT NULL,
  `id_usuario` int NOT NULL,
  `asignado_por` int NOT NULL,
  `asignado_en` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `devuelto_en` datetime DEFAULT NULL,
  `ruta_pdf` varchar(300) DEFAULT NULL,
  PRIMARY KEY (`id_asignacion`),
  UNIQUE KEY `uq_equipo_asignado_activo` (`id_equipo`,`asignado_en`),
  KEY `idx_asignacion_usuario` (`id_usuario`),
  KEY `asignado_por` (`asignado_por`),
  CONSTRAINT `asignacion_ibfk_1` FOREIGN KEY (`id_equipo`) REFERENCES `equipo` (`id_equipo`),
  CONSTRAINT `asignacion_ibfk_2` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `asignacion_ibfk_3` FOREIGN KEY (`asignado_por`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `asignacion_chk_1` CHECK (((`devuelto_en` is null) or (`devuelto_en` >= `asignado_en`)))
) ENGINE=InnoDB AUTO_INCREMENT=4003 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `asignacion`
--

LOCK TABLES `asignacion` WRITE;
/*!40000 ALTER TABLE `asignacion` DISABLE KEYS */;
INSERT INTO `asignacion` VALUES (4001,2301,1001,1005,'2025-08-18 12:27:45','2025-08-18 13:08:23','/docs/asignaciones/4001_luisperez_lat5420.pdf'),(4002,3001,1003,1005,'2025-08-19 09:49:03',NULL,'/docs/asignaciones/4002_javierlopez_sim3001.pdf');
/*!40000 ALTER TABLE `asignacion` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_asignacion_before_insert` BEFORE INSERT ON `asignacion` FOR EACH ROW BEGIN
  DECLARE est_actual int;
  SELECT id_estatus INTO est_actual FROM equipo WHERE id_equipo = NEW.id_equipo FOR UPDATE;
  IF est_actual IS NULL THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Equipo inexistente';
  END IF;
  IF est_actual <> 1 THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Solo se pueden asignar equipos con estatus Disponible';
  END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_asignacion_after_insert` AFTER INSERT ON `asignacion` FOR EACH ROW BEGIN
  UPDATE equipo SET id_estatus = 2 WHERE id_equipo = NEW.id_equipo;
  INSERT INTO bitacora_movimiento (id_equipo,id_usuario,accion,estatus_origen,estatus_destino,realizado_por,notas)
  VALUES (NEW.id_equipo, NEW.id_usuario, 'ASIGNAR', 1, 2, NEW.asignado_por, 'Asignación creada');
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_asignacion_after_update` AFTER UPDATE ON `asignacion` FOR EACH ROW BEGIN
  IF NEW.devuelto_en IS NOT NULL AND OLD.devuelto_en IS NULL THEN
    UPDATE equipo SET id_estatus = 1 WHERE id_equipo = NEW.id_equipo;
    INSERT INTO bitacora_movimiento (id_equipo,id_usuario,accion,estatus_origen,estatus_destino,realizado_por,notas)
    VALUES (NEW.id_equipo, NEW.id_usuario, 'DEVOLVER', 2, 1, NEW.asignado_por, 'Equipo devuelto');
  END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `bitacora_movimiento`
--

DROP TABLE IF EXISTS `bitacora_movimiento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bitacora_movimiento` (
  `id_mov` int NOT NULL AUTO_INCREMENT,
  `id_equipo` int NOT NULL,
  `id_usuario` int DEFAULT NULL,
  `accion` enum('ASIGNAR','DEVOLVER','CAMBIO_ESTATUS','REPARACION_IN','REPARACION_OUT') NOT NULL,
  `estatus_origen` int DEFAULT NULL,
  `estatus_destino` int DEFAULT NULL,
  `realizado_por` int NOT NULL,
  `realizado_en` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `notas` text,
  PRIMARY KEY (`id_mov`),
  KEY `idx_bitacora_equipo` (`id_equipo`),
  KEY `id_usuario` (`id_usuario`),
  KEY `estatus_origen` (`estatus_origen`),
  KEY `estatus_destino` (`estatus_destino`),
  KEY `realizado_por` (`realizado_por`),
  CONSTRAINT `bitacora_movimiento_ibfk_1` FOREIGN KEY (`id_equipo`) REFERENCES `equipo` (`id_equipo`),
  CONSTRAINT `bitacora_movimiento_ibfk_2` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `bitacora_movimiento_ibfk_3` FOREIGN KEY (`estatus_origen`) REFERENCES `estatus` (`id_estatus`),
  CONSTRAINT `bitacora_movimiento_ibfk_4` FOREIGN KEY (`estatus_destino`) REFERENCES `estatus` (`id_estatus`),
  CONSTRAINT `bitacora_movimiento_ibfk_5` FOREIGN KEY (`realizado_por`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bitacora_movimiento`
--

LOCK TABLES `bitacora_movimiento` WRITE;
/*!40000 ALTER TABLE `bitacora_movimiento` DISABLE KEYS */;
INSERT INTO `bitacora_movimiento` VALUES (1,2301,1001,'ASIGNAR',1,2,1005,'2025-08-13 10:38:07','Asignación creada'),(2,3001,1003,'ASIGNAR',1,2,1005,'2025-08-13 10:38:07','Asignación creada'),(3,2301,1001,'ASIGNAR',1,2,1005,'2025-08-14 13:10:46','Asignación creada'),(4,3001,1003,'ASIGNAR',1,2,1005,'2025-08-14 13:11:05','Asignación creada'),(5,2301,1001,'ASIGNAR',1,2,1005,'2025-08-14 13:22:19','Asignación creada'),(6,2301,1001,'ASIGNAR',1,2,1005,'2025-08-14 13:27:35','Asignación creada'),(7,2301,1001,'ASIGNAR',1,2,1005,'2025-08-18 12:27:45','Asignación creada'),(8,2301,1001,'DEVOLVER',2,1,1005,'2025-08-18 13:08:23','Equipo devuelto'),(9,3001,1003,'ASIGNAR',1,2,1005,'2025-08-19 09:49:03','Asignación creada');
/*!40000 ALTER TABLE `bitacora_movimiento` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `centro`
--

DROP TABLE IF EXISTS `centro`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `centro` (
  `id_centro` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  `id_ubicacion` int NOT NULL,
  `notas` text,
  PRIMARY KEY (`id_centro`),
  KEY `id_ubicacion` (`id_ubicacion`),
  CONSTRAINT `centro_ibfk_1` FOREIGN KEY (`id_ubicacion`) REFERENCES `ubicacion` (`id_ubicacion`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `centro`
--

LOCK TABLES `centro` WRITE;
/*!40000 ALTER TABLE `centro` DISABLE KEYS */;
INSERT INTO `centro` VALUES (1,'CTI-CORP',1,'Centro de costo de TI en CDMX'),(2,'PL1-OPS',2,'Operaciones Planta 1'),(3,'GDL-DES',3,'Desarrollo Guadalajara');
/*!40000 ALTER TABLE `centro` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `color`
--

DROP TABLE IF EXISTS `color`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `color` (
  `id_color` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(60) NOT NULL,
  `hex` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id_color`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `color`
--

LOCK TABLES `color` WRITE;
/*!40000 ALTER TABLE `color` DISABLE KEYS */;
INSERT INTO `color` VALUES (1,'Negro','#000000'),(2,'Cian','#00FFFF'),(3,'Magenta','#FF00FF'),(4,'Amarillo','FFFF00');
/*!40000 ALTER TABLE `color` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `equipo`
--

DROP TABLE IF EXISTS `equipo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `equipo` (
  `id_equipo` int NOT NULL AUTO_INCREMENT,
  `id_tipo` int NOT NULL,
  `id_modelo` int DEFAULT NULL,
  `numero_serie` varchar(120) DEFAULT NULL,
  `id_marca` int DEFAULT NULL,
  `id_ubicacion` int DEFAULT NULL,
  `id_estatus` int NOT NULL DEFAULT '1',
  `ip_fija` varchar(45) DEFAULT NULL,
  `puerto_ethernet` varchar(20) DEFAULT NULL,
  `notas` text,
  PRIMARY KEY (`id_equipo`),
  UNIQUE KEY `numero_serie` (`numero_serie`),
  KEY `idx_equipo_estatus` (`id_estatus`),
  KEY `idx_equipo_tipo` (`id_tipo`),
  KEY `idx_equipo_ubicacion` (`id_ubicacion`),
  KEY `id_modelo` (`id_modelo`),
  KEY `id_marca` (`id_marca`),
  CONSTRAINT `equipo_ibfk_1` FOREIGN KEY (`id_tipo`) REFERENCES `tipo_equipo` (`id_tipo`),
  CONSTRAINT `equipo_ibfk_2` FOREIGN KEY (`id_modelo`) REFERENCES `modelo` (`id_modelo`),
  CONSTRAINT `equipo_ibfk_3` FOREIGN KEY (`id_marca`) REFERENCES `marca` (`id_marca`),
  CONSTRAINT `equipo_ibfk_4` FOREIGN KEY (`id_ubicacion`) REFERENCES `ubicacion` (`id_ubicacion`),
  CONSTRAINT `equipo_ibfk_5` FOREIGN KEY (`id_estatus`) REFERENCES `estatus` (`id_estatus`)
) ENGINE=InnoDB AUTO_INCREMENT=3103 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `equipo`
--

LOCK TABLES `equipo` WRITE;
/*!40000 ALTER TABLE `equipo` DISABLE KEYS */;
INSERT INTO `equipo` VALUES (2001,1,1,'HP-M404-0001',1,1,1,'10.10.1.21',NULL,'Impresora piso 2'),(2002,1,6,'EPS-L3250-0001',6,2,1,'10.20.1.15',NULL,'Falla de tarjeta lógica'),(2101,2,3,'CISC-AP1850-0001',3,1,3,'10.10.2.31',NULL,NULL),(2201,3,7,'DELL-WS7090-0001',2,2,1,NULL,NULL,'Control de línea A'),(2301,4,2,'DELL-LAT5420-0001',2,1,1,NULL,NULL,'SSD 512GB, 16GB RAM'),(2302,4,8,'HP-ELITE840-0001',1,3,1,NULL,NULL,NULL),(2304,4,2,'DELL-LAT5420-0002',2,2,1,NULL,NULL,'SSD 256GB, 8GB RAM'),(2305,4,4,'APL-MBP14-0002',4,3,1,NULL,NULL,'Equipo QA'),(2401,5,2,'MON-GEN-0001',2,1,1,NULL,NULL,'24 pulgadas'),(2402,5,NULL,'MON-GEN-0002',2,1,1,NULL,NULL,'27 pulgadas UHD'),(2403,5,NULL,'MON-GEN-0003',4,2,1,NULL,NULL,'32 pulgadas curved'),(2601,6,12,'CISC-CP7841-0001',3,1,1,'10.30.1.40',NULL,'Extensión recepción'),(2602,6,12,'CISC-CP7841-0002',3,2,1,'10.30.1.41',NULL,'Extensión RH'),(2701,7,9,'MOT-G54-0001',7,2,1,NULL,NULL,NULL),(2702,7,15,'SAM-GALAXY-S22-01',5,1,1,NULL,NULL,'Equipo gerencia'),(2703,7,16,'APL-IPHONE13-0001',4,2,1,NULL,NULL,'Equipo dirección'),(2801,8,5,'SMS-TABS7-0001',5,1,1,NULL,NULL,NULL),(2802,8,10,'SMS-TAB3-0001',5,1,1,NULL,NULL,NULL),(2804,8,11,'SMS-TAB5-0002',5,2,1,NULL,NULL,NULL),(2903,9,13,'ZEB-HHMC93-0001',8,3,1,NULL,NULL,'Uso en almacén A'),(2904,9,13,'ZEB-HHMC93-0002',8,3,1,NULL,NULL,'Uso en almacén B'),(2905,11,1,'CN-HPM404-MAG-0001',1,1,1,NULL,NULL,'Tóner Magenta'),(2906,11,1,'CN-HPM404-YEL-0001',1,1,1,NULL,NULL,'Tóner Amarillo'),(3001,8,4,'SIM-ATTMX-0001',4,1,2,NULL,NULL,'Plan corporativo'),(3003,12,NULL,'SIM-ATTMX-0003',NULL,3,1,NULL,NULL,'Plan corporativo'),(3004,12,NULL,'SIM-ATTMX-000111',NULL,2,1,NULL,NULL,'Plan corporativo Telcel'),(3006,12,NULL,NULL,1,NULL,1,NULL,NULL,NULL),(3007,11,NULL,NULL,NULL,NULL,1,NULL,NULL,'Tinta'),(3101,10,14,'UBNT-NANOSTA-0001',9,2,1,NULL,NULL,'Enlace a sucursal norte'),(3102,10,14,'UBNT-NANOSTA-0002',9,3,11,NULL,NULL,'Enlace a sucursal sur');
/*!40000 ALTER TABLE `equipo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `equipo_consumible`
--

DROP TABLE IF EXISTS `equipo_consumible`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `equipo_consumible` (
  `id_equipo` int NOT NULL,
  `id_color` int NOT NULL,
  PRIMARY KEY (`id_equipo`),
  KEY `id_color` (`id_color`),
  CONSTRAINT `equipo_consumible_ibfk_1` FOREIGN KEY (`id_equipo`) REFERENCES `equipo` (`id_equipo`) ON DELETE CASCADE,
  CONSTRAINT `equipo_consumible_ibfk_2` FOREIGN KEY (`id_color`) REFERENCES `color` (`id_color`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `equipo_consumible`
--

LOCK TABLES `equipo_consumible` WRITE;
/*!40000 ALTER TABLE `equipo_consumible` DISABLE KEYS */;
INSERT INTO `equipo_consumible` VALUES (3007,1),(2905,3),(2906,4);
/*!40000 ALTER TABLE `equipo_consumible` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `equipo_sim`
--

DROP TABLE IF EXISTS `equipo_sim`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `equipo_sim` (
  `id_equipo` int NOT NULL,
  `numero_asignado` varchar(30) NOT NULL,
  `imei` varchar(20) NOT NULL,
  PRIMARY KEY (`id_equipo`),
  UNIQUE KEY `numero_asignado` (`numero_asignado`),
  UNIQUE KEY `imei` (`imei`),
  CONSTRAINT `equipo_sim_ibfk_1` FOREIGN KEY (`id_equipo`) REFERENCES `equipo` (`id_equipo`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `equipo_sim`
--

LOCK TABLES `equipo_sim` WRITE;
/*!40000 ALTER TABLE `equipo_sim` DISABLE KEYS */;
INSERT INTO `equipo_sim` VALUES (3001,'554000001','356789012345671'),(3006,'2299787822','356789012345672');
/*!40000 ALTER TABLE `equipo_sim` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `estatus`
--

DROP TABLE IF EXISTS `estatus`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `estatus` (
  `id_estatus` int NOT NULL AUTO_INCREMENT,
  `tipo_estatus` enum('EQUIPO','PERSONAL','UBICACION','UNIVERSAL') DEFAULT NULL,
  `nombre` varchar(20) NOT NULL,
  PRIMARY KEY (`id_estatus`),
  UNIQUE KEY `tipo_estatus` (`tipo_estatus`,`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `estatus`
--

LOCK TABLES `estatus` WRITE;
/*!40000 ALTER TABLE `estatus` DISABLE KEYS */;
INSERT INTO `estatus` VALUES (2,'EQUIPO','Asignado'),(4,'EQUIPO','Desuso'),(1,'EQUIPO','Disponible'),(3,'EQUIPO','En reparación'),(9,'PERSONAL','Activo'),(10,'PERSONAL','Baja'),(7,'UBICACION','Activa'),(8,'UBICACION','Inactiva'),(11,'UNIVERSAL','Eliminado');
/*!40000 ALTER TABLE `estatus` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lider`
--

DROP TABLE IF EXISTS `lider`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lider` (
  `id_lider` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  `apellido_paterno` varchar(50) DEFAULT NULL,
  `apellido_materno` varchar(50) DEFAULT NULL,
  `email` varchar(120) NOT NULL,
  `telefono` varchar(25) DEFAULT NULL,
  `id_centro` int NOT NULL,
  `id_puesto` int NOT NULL,
  `id_estatus` int NOT NULL,
  PRIMARY KEY (`id_lider`),
  UNIQUE KEY `email` (`email`),
  KEY `id_centro` (`id_centro`),
  KEY `id_puesto` (`id_puesto`),
  KEY `id_estatus` (`id_estatus`),
  CONSTRAINT `lider_ibfk_1` FOREIGN KEY (`id_centro`) REFERENCES `centro` (`id_centro`),
  CONSTRAINT `lider_ibfk_2` FOREIGN KEY (`id_puesto`) REFERENCES `puesto` (`id_puesto`),
  CONSTRAINT `lider_ibfk_3` FOREIGN KEY (`id_estatus`) REFERENCES `estatus` (`id_estatus`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lider`
--

LOCK TABLES `lider` WRITE;
/*!40000 ALTER TABLE `lider` DISABLE KEYS */;
INSERT INTO `lider` VALUES (1,'María','García','Luna','maria.garcia@empresa.com','555-100-0001',1,3,9),(2,'Carlos','Rivas','Mora','carlos.rivas@empresa.com','818-200-0002',2,3,9),(3,'Ana','Torres','Ibarra','ana.torres@empresa.com','333-300-0003',3,3,9);
/*!40000 ALTER TABLE `lider` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `marca`
--

DROP TABLE IF EXISTS `marca`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `marca` (
  `id_marca` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  `notas` text,
  PRIMARY KEY (`id_marca`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `marca`
--

LOCK TABLES `marca` WRITE;
/*!40000 ALTER TABLE `marca` DISABLE KEYS */;
INSERT INTO `marca` VALUES (1,'HP',1,NULL),(2,'Dell',1,NULL),(3,'Cisco',1,NULL),(4,'Apple',1,NULL),(5,'Samsung',1,NULL),(6,'Epson',1,NULL),(7,'Motorola',1,NULL),(8,'Zebra',1,NULL),(9,'Ubiquiti',1,NULL);
/*!40000 ALTER TABLE `marca` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `modelo`
--

DROP TABLE IF EXISTS `modelo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `modelo` (
  `id_modelo` int NOT NULL AUTO_INCREMENT,
  `id_marca` int NOT NULL,
  `nombre` varchar(120) NOT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  `notas` text,
  PRIMARY KEY (`id_modelo`),
  UNIQUE KEY `id_marca` (`id_marca`,`nombre`),
  CONSTRAINT `modelo_ibfk_1` FOREIGN KEY (`id_marca`) REFERENCES `marca` (`id_marca`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `modelo`
--

LOCK TABLES `modelo` WRITE;
/*!40000 ALTER TABLE `modelo` DISABLE KEYS */;
INSERT INTO `modelo` VALUES (1,1,'LaserJet M404dn',1,'Impresora monocromo'),(2,2,'Latitude 5420',1,'Laptop corporativa'),(3,3,'Aironet 1850',1,'Access Point WiFi'),(4,4,'MacBook Pro 14\"',1,'Laptop desarrollo'),(5,5,'Galaxy Tab S7',1,'Tablet Android'),(6,6,'EcoTank L3250',1,'Multifuncional'),(7,2,'Precision 7090',1,'Workstation de alto desempeño'),(8,1,'EliteBook 840',1,'Laptop corporativa'),(9,7,'Moto G54',1,'Celular Android'),(10,5,'Galaxy Tab 3',1,'Tablet Android'),(11,5,'Galaxy Tab 5',1,'Tablet Android'),(12,4,'Cisco 7841',1,'Teléfono IP fijo'),(13,8,'MC9300',1,'Handheld rugerizado'),(14,9,'NanoStation AC',1,'Enlace inalámbrico'),(15,5,'Galaxy S22',1,'Celular corporativo'),(16,4,'iPhone 13',1,'Celular corporativo');
/*!40000 ALTER TABLE `modelo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `puesto`
--

DROP TABLE IF EXISTS `puesto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `puesto` (
  `id_puesto` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(120) NOT NULL,
  `notas` text,
  PRIMARY KEY (`id_puesto`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `puesto`
--

LOCK TABLES `puesto` WRITE;
/*!40000 ALTER TABLE `puesto` DISABLE KEYS */;
INSERT INTO `puesto` VALUES (1,'Analista de Soporte','N1/N2'),(2,'Administrador de Sistemas','Infraestructura'),(3,'Gerente de TI','Lidera el área'),(4,'Operador de Planta','Producción'),(5,'Desarrollador','Aplicaciones internas');
/*!40000 ALTER TABLE `puesto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rol`
--

DROP TABLE IF EXISTS `rol`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rol` (
  `id_rol` int NOT NULL,
  `nombre` varchar(20) NOT NULL,
  PRIMARY KEY (`id_rol`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rol`
--

LOCK TABLES `rol` WRITE;
/*!40000 ALTER TABLE `rol` DISABLE KEYS */;
INSERT INTO `rol` VALUES (2,'admin'),(1,'empleado'),(3,'master');
/*!40000 ALTER TABLE `rol` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tipo_equipo`
--

DROP TABLE IF EXISTS `tipo_equipo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tipo_equipo` (
  `id_tipo` int NOT NULL,
  `nombre` varchar(40) NOT NULL,
  PRIMARY KEY (`id_tipo`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tipo_equipo`
--

LOCK TABLES `tipo_equipo` WRITE;
/*!40000 ALTER TABLE `tipo_equipo` DISABLE KEYS */;
INSERT INTO `tipo_equipo` VALUES (2,'Access Point'),(10,'Antena'),(7,'Celular'),(9,'Computadora móvil'),(11,'Consumible'),(1,'Impresora'),(4,'Laptop'),(5,'Monitor'),(12,'SIM'),(8,'Tablet'),(6,'Teléfono fijo'),(3,'Workstation');
/*!40000 ALTER TABLE `tipo_equipo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ubicacion`
--

DROP TABLE IF EXISTS `ubicacion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ubicacion` (
  `id_ubicacion` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(120) NOT NULL,
  `id_estatus` int NOT NULL,
  `notas` text,
  PRIMARY KEY (`id_ubicacion`),
  UNIQUE KEY `nombre` (`nombre`),
  KEY `id_estatus` (`id_estatus`),
  CONSTRAINT `ubicacion_ibfk_1` FOREIGN KEY (`id_estatus`) REFERENCES `estatus` (`id_estatus`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ubicacion`
--

LOCK TABLES `ubicacion` WRITE;
/*!40000 ALTER TABLE `ubicacion` DISABLE KEYS */;
INSERT INTO `ubicacion` VALUES (1,'CDMX - Corporativo',7,'Sede principal'),(2,'Monterrey - Planta 1',7,'Producción'),(3,'Guadalajara - Centro TI',7,'Soporte y desarrollo');
/*!40000 ALTER TABLE `ubicacion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `id_usuario` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  `apellido_paterno` varchar(50) DEFAULT NULL,
  `apellido_materno` varchar(50) DEFAULT NULL,
  `email` varchar(120) NOT NULL,
  `telefono` varchar(25) DEFAULT NULL,
  `id_lider` int NOT NULL,
  `id_puesto` int NOT NULL,
  `id_centro` int NOT NULL,
  `id_rol` int NOT NULL,
  `hash_password` varchar(255) NOT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  `ultimo_login` datetime DEFAULT NULL,
  `creado_en` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_usuario`),
  UNIQUE KEY `email` (`email`),
  KEY `id_lider` (`id_lider`),
  KEY `id_puesto` (`id_puesto`),
  KEY `id_centro` (`id_centro`),
  KEY `id_rol` (`id_rol`),
  CONSTRAINT `usuario_ibfk_1` FOREIGN KEY (`id_lider`) REFERENCES `lider` (`id_lider`),
  CONSTRAINT `usuario_ibfk_2` FOREIGN KEY (`id_puesto`) REFERENCES `puesto` (`id_puesto`),
  CONSTRAINT `usuario_ibfk_3` FOREIGN KEY (`id_centro`) REFERENCES `centro` (`id_centro`),
  CONSTRAINT `usuario_ibfk_4` FOREIGN KEY (`id_rol`) REFERENCES `rol` (`id_rol`)
) ENGINE=InnoDB AUTO_INCREMENT=1010 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES (1001,'Luis','Pérez','Soto','luis.perez@empresa.com','555-110-0001',1,1,1,1,'$2y$10$2bExAMPLE1u8kS1s9cVQEeWw9dC8T9q1O6q7ZV3m2o1t1nq5y3xVO',1,NULL,'2025-08-13 10:38:07'),(1002,'Elena','Ruiz','Campos','elena.ruiz@empresa.com','555-110-0002',1,5,1,1,'$2y$10$2bExAMPLE2u8kS1s9cVQEeWw9dC8T9q1O6q7ZV3m2o1t1nq5y3xVO',1,NULL,'2025-08-13 10:38:07'),(1003,'Javier','López','Nieto','javier.lopez@empresa.com','818-220-0003',2,4,2,1,'$2y$10$2bExAMPLE3u8kS1s9cVQEeWw9dC8T9q1O6q7ZV3m2o1t1nq5y3xVO',1,NULL,'2025-08-13 10:38:07'),(1004,'Sofía','Martínez','Ortega','sofia.martinez@empresa.com','333-330-0004',3,5,3,1,'$2y$10$2bExAMPLE4u8kS1s9cVQEeWw9dC8T9q1O6q7ZV3m2o1t1nq5y3xVO',1,NULL,'2025-08-13 10:38:07'),(1005,'Admin','TI','','admin.ti@empresa.com','555-000-9000',1,2,1,2,'$2y$10$AdMiNPaSSWORDEXAMPLEaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa',1,NULL,'2025-08-13 10:38:07'),(1006,'Master','System','','master.sys@empresa.com','555-000-9999',1,3,1,3,'$2y$10$MaSTERPaSSWORDEXAMPLEbbbbbbbbbbbbbbbbbbbbbbbbbbbb',1,NULL,'2025-08-13 10:38:07'),(1008,'Osmar','Hernández','Durán','zs20004630@estudiantes.uv.mx','2299787822',3,2,1,1,'pbkdf2$150000$o6zHC4RgeV8B04wRILgXZA==$whT2Cv1h7/MKEOn0IyNbUKusoXWK4J6+t9lEZVFrvg0=',1,'2025-08-22 12:23:59','2025-08-20 12:05:34'),(1009,'Osmar','Hernández','Durán','osmarhdezduran@gmail.com','2299787822',3,2,1,1,'pbkdf2$150000$0YNUnPrheFrIyIpKqHkYkA==$16BDD89y5iRZuoTIpTu6lFJB4+9psdwftMsIo2Ye+hE=',1,NULL,'2025-08-20 12:31:11');
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `vw_dashboard_resumen`
--

DROP TABLE IF EXISTS `vw_dashboard_resumen`;
/*!50001 DROP VIEW IF EXISTS `vw_dashboard_resumen`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `vw_dashboard_resumen` AS SELECT 
 1 AS `total_usuarios`,
 1 AS `total_equipos_asignados`,
 1 AS `equipos_disponibles`,
 1 AS `equipos_en_reparacion`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `vw_equipos_asignados`
--

DROP TABLE IF EXISTS `vw_equipos_asignados`;
/*!50001 DROP VIEW IF EXISTS `vw_equipos_asignados`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `vw_equipos_asignados` AS SELECT 
 1 AS `id_equipo`,
 1 AS `id_tipo`,
 1 AS `id_modelo`,
 1 AS `numero_serie`,
 1 AS `id_marca`,
 1 AS `id_ubicacion`,
 1 AS `id_estatus`,
 1 AS `ip_fija`,
 1 AS `puerto_ethernet`,
 1 AS `notas`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `vw_equipos_disponibles`
--

DROP TABLE IF EXISTS `vw_equipos_disponibles`;
/*!50001 DROP VIEW IF EXISTS `vw_equipos_disponibles`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `vw_equipos_disponibles` AS SELECT 
 1 AS `id_equipo`,
 1 AS `id_tipo`,
 1 AS `id_modelo`,
 1 AS `numero_serie`,
 1 AS `id_marca`,
 1 AS `id_ubicacion`,
 1 AS `id_estatus`,
 1 AS `ip_fija`,
 1 AS `puerto_ethernet`,
 1 AS `notas`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `vw_equipos_reparacion`
--

DROP TABLE IF EXISTS `vw_equipos_reparacion`;
/*!50001 DROP VIEW IF EXISTS `vw_equipos_reparacion`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `vw_equipos_reparacion` AS SELECT 
 1 AS `id_equipo`,
 1 AS `id_tipo`,
 1 AS `id_modelo`,
 1 AS `numero_serie`,
 1 AS `id_marca`,
 1 AS `id_ubicacion`,
 1 AS `id_estatus`,
 1 AS `ip_fija`,
 1 AS `puerto_ethernet`,
 1 AS `notas`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `vw_sim_disponibles`
--

DROP TABLE IF EXISTS `vw_sim_disponibles`;
/*!50001 DROP VIEW IF EXISTS `vw_sim_disponibles`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `vw_sim_disponibles` AS SELECT 
 1 AS `id_equipo`,
 1 AS `numero_asignado`,
 1 AS `imei`*/;
SET character_set_client = @saved_cs_client;

--
-- Dumping events for database 'gmas2'
--

--
-- Dumping routines for database 'gmas2'
--

--
-- Final view structure for view `vw_dashboard_resumen`
--

/*!50001 DROP VIEW IF EXISTS `vw_dashboard_resumen`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `vw_dashboard_resumen` AS select (select count(0) from `usuario` where (`usuario`.`activo` = true)) AS `total_usuarios`,(select count(0) from `equipo` where (`equipo`.`id_estatus` = 2)) AS `total_equipos_asignados`,(select count(0) from `equipo` where (`equipo`.`id_estatus` = 1)) AS `equipos_disponibles`,(select count(0) from `equipo` where (`equipo`.`id_estatus` = 3)) AS `equipos_en_reparacion` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `vw_equipos_asignados`
--

/*!50001 DROP VIEW IF EXISTS `vw_equipos_asignados`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `vw_equipos_asignados` AS select `e`.`id_equipo` AS `id_equipo`,`e`.`id_tipo` AS `id_tipo`,`e`.`id_modelo` AS `id_modelo`,`e`.`numero_serie` AS `numero_serie`,`e`.`id_marca` AS `id_marca`,`e`.`id_ubicacion` AS `id_ubicacion`,`e`.`id_estatus` AS `id_estatus`,`e`.`ip_fija` AS `ip_fija`,`e`.`puerto_ethernet` AS `puerto_ethernet`,`e`.`notas` AS `notas` from `equipo` `e` where (`e`.`id_estatus` = 2) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `vw_equipos_disponibles`
--

/*!50001 DROP VIEW IF EXISTS `vw_equipos_disponibles`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `vw_equipos_disponibles` AS select `e`.`id_equipo` AS `id_equipo`,`e`.`id_tipo` AS `id_tipo`,`e`.`id_modelo` AS `id_modelo`,`e`.`numero_serie` AS `numero_serie`,`e`.`id_marca` AS `id_marca`,`e`.`id_ubicacion` AS `id_ubicacion`,`e`.`id_estatus` AS `id_estatus`,`e`.`ip_fija` AS `ip_fija`,`e`.`puerto_ethernet` AS `puerto_ethernet`,`e`.`notas` AS `notas` from `equipo` `e` where (`e`.`id_estatus` = 1) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `vw_equipos_reparacion`
--

/*!50001 DROP VIEW IF EXISTS `vw_equipos_reparacion`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `vw_equipos_reparacion` AS select `e`.`id_equipo` AS `id_equipo`,`e`.`id_tipo` AS `id_tipo`,`e`.`id_modelo` AS `id_modelo`,`e`.`numero_serie` AS `numero_serie`,`e`.`id_marca` AS `id_marca`,`e`.`id_ubicacion` AS `id_ubicacion`,`e`.`id_estatus` AS `id_estatus`,`e`.`ip_fija` AS `ip_fija`,`e`.`puerto_ethernet` AS `puerto_ethernet`,`e`.`notas` AS `notas` from `equipo` `e` where (`e`.`id_estatus` = 3) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `vw_sim_disponibles`
--

/*!50001 DROP VIEW IF EXISTS `vw_sim_disponibles`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `vw_sim_disponibles` AS select `es`.`id_equipo` AS `id_equipo`,`es`.`numero_asignado` AS `numero_asignado`,`es`.`imei` AS `imei` from (`equipo` `e` join `equipo_sim` `es` on((`es`.`id_equipo` = `e`.`id_equipo`))) where (`e`.`id_estatus` = 1) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-08-22 12:54:14
