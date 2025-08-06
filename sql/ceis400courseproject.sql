-- MySQL dump 10.13  Distrib 8.0.38, for Win64 (x86_64)
--
-- Host: localhost    Database: ceis400courseproject
-- ------------------------------------------------------
-- Server version	8.0.38

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
-- Table structure for table `employee`
--

DROP TABLE IF EXISTS `employee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employee` (
  `empID` int NOT NULL,
  `empName` varchar(20) NOT NULL,
  `skillClassification` enum('Electrician','Painter','Plumber','Welder','Carpenter') NOT NULL,
  PRIMARY KEY (`empID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employee`
--

LOCK TABLES `employee` WRITE;
/*!40000 ALTER TABLE `employee` DISABLE KEYS */;
INSERT INTO `employee` VALUES (1,'Jorge','Electrician'),(2,'Zachary','Painter'),(3,'Raymond','Carpenter'),(4,'Megan','Welder'),(5,'David','Plumber');
/*!40000 ALTER TABLE `employee` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `equipment`
--

DROP TABLE IF EXISTS `equipment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `equipment` (
  `equipmentID` int NOT NULL AUTO_INCREMENT,
  `equipmentName` varchar(20) NOT NULL,
  `equipmentCondition` enum('Good','Damaged','Lost') NOT NULL,
  `requiredSkill` enum('Electrician','Painter','Plumber','Welder','Carpenter') NOT NULL,
  `equipStatus` enum('Lost','Loaned','Available','Ordered') NOT NULL DEFAULT 'Available',
  PRIMARY KEY (`equipmentID`)
) ENGINE=InnoDB AUTO_INCREMENT=113 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `equipment`
--

LOCK TABLES `equipment` WRITE;
/*!40000 ALTER TABLE `equipment` DISABLE KEYS */;
INSERT INTO `equipment` VALUES (100,'Hammer','Good','Carpenter','Loaned'),(101,'Paint Brush','Good','Painter','Loaned'),(102,'Torch','Good','Welder','Loaned'),(103,'Saw','Good','Plumber','Loaned'),(104,'Hacksaw','Good','Plumber','Loaned'),(105,'Paint Roller','Good','Painter','Loaned'),(106,'Chisel','Good','Carpenter','Loaned'),(107,'Screwdriver Set','Good','Carpenter','Loaned'),(108,'Voltage Tester','Good','Electrician','Loaned'),(109,'Wire Stripper','Good','Electrician','Loaned'),(110,'Heat gun','Good','Painter','Loaned'),(111,'Welding Helmet','Good','Welder','Loaned'),(112,'Gloves','Good','Welder','Loaned');
/*!40000 ALTER TABLE `equipment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order`
--

DROP TABLE IF EXISTS `order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order` (
  `orderID` int NOT NULL AUTO_INCREMENT,
  `empID` int NOT NULL,
  `equipmentID` int NOT NULL,
  `orderDate` date NOT NULL,
  `pickUpDate` date DEFAULT NULL,
  `orderStatus` enum('Pending','Confirmed','Cancelled') DEFAULT NULL,
  PRIMARY KEY (`orderID`),
  KEY `empID` (`empID`),
  KEY `order_ibfk_2` (`equipmentID`),
  CONSTRAINT `order_ibfk_1` FOREIGN KEY (`empID`) REFERENCES `employee` (`empID`),
  CONSTRAINT `order_ibfk_2` FOREIGN KEY (`equipmentID`) REFERENCES `equipment` (`equipmentID`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order`
--

LOCK TABLES `order` WRITE;
/*!40000 ALTER TABLE `order` DISABLE KEYS */;
INSERT INTO `order` VALUES (1,1,100,'2025-07-15',NULL,'Cancelled'),(2,2,101,'2025-07-16',NULL,'Cancelled'),(3,3,102,'2025-07-17',NULL,'Cancelled'),(4,4,103,'2025-07-18',NULL,'Cancelled'),(5,5,104,'2025-07-19',NULL,'Cancelled'),(6,1,108,'2025-08-05',NULL,'Cancelled'),(7,1,109,'2025-08-05',NULL,'Cancelled'),(8,3,100,'2025-08-05',NULL,'Cancelled'),(9,3,106,'2025-08-05',NULL,'Cancelled'),(10,3,107,'2025-08-05',NULL,'Cancelled'),(11,3,107,'2025-08-05',NULL,'Cancelled'),(12,3,100,'2025-08-05',NULL,'Cancelled'),(13,3,106,'2025-08-05',NULL,'Cancelled'),(14,3,106,'2025-08-05',NULL,'Cancelled'),(15,5,103,'2025-08-05',NULL,'Cancelled');
/*!40000 ALTER TABLE `order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reminder`
--

DROP TABLE IF EXISTS `reminder`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reminder` (
  `reminderID` int NOT NULL AUTO_INCREMENT,
  `empID` int NOT NULL,
  `transactionID` int NOT NULL,
  `reminderDate` date NOT NULL,
  `reminderMSG` text,
  PRIMARY KEY (`reminderID`),
  KEY `empID` (`empID`),
  KEY `transactionID` (`transactionID`),
  CONSTRAINT `reminder_ibfk_1` FOREIGN KEY (`empID`) REFERENCES `employee` (`empID`),
  CONSTRAINT `reminder_ibfk_2` FOREIGN KEY (`transactionID`) REFERENCES `transaction` (`transactionID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reminder`
--

LOCK TABLES `reminder` WRITE;
/*!40000 ALTER TABLE `reminder` DISABLE KEYS */;
/*!40000 ALTER TABLE `reminder` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transaction`
--

DROP TABLE IF EXISTS `transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transaction` (
  `transactionID` int NOT NULL AUTO_INCREMENT,
  `empID` int NOT NULL,
  `equipmentID` int NOT NULL,
  `orderID` int DEFAULT NULL,
  `borrowDate` date NOT NULL,
  `expectedReturnDate` date DEFAULT NULL,
  `transactionStatus` enum('Borrowed','Returned','Late','Cancelled') DEFAULT NULL,
  `returnDate` date DEFAULT NULL,
  PRIMARY KEY (`transactionID`),
  KEY `empID` (`empID`),
  KEY `orderID` (`orderID`),
  KEY `transaction_ibfk_2` (`equipmentID`),
  CONSTRAINT `transaction_ibfk_1` FOREIGN KEY (`empID`) REFERENCES `employee` (`empID`),
  CONSTRAINT `transaction_ibfk_2` FOREIGN KEY (`equipmentID`) REFERENCES `equipment` (`equipmentID`),
  CONSTRAINT `transaction_ibfk_3` FOREIGN KEY (`orderID`) REFERENCES `order` (`orderID`)
) ENGINE=InnoDB AUTO_INCREMENT=147 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction`
--

LOCK TABLES `transaction` WRITE;
/*!40000 ALTER TABLE `transaction` DISABLE KEYS */;
INSERT INTO `transaction` VALUES (13,1,102,NULL,'2025-07-31','2025-08-07','Returned','2025-08-01'),(14,2,103,NULL,'2025-07-31','2025-08-05','Returned','2025-08-01'),(15,3,104,NULL,'2025-07-31','2025-08-03','Returned','2025-08-01'),(16,5,103,NULL,'2025-07-31','2025-09-18','Returned','2025-08-01'),(17,4,102,NULL,'2025-07-31','2025-09-18','Returned','2025-08-01'),(18,5,104,NULL,'2025-07-31','2025-09-18','Returned','2025-08-01'),(19,5,104,NULL,'2025-07-31','2025-09-18','Returned','2025-08-01'),(20,5,104,NULL,'2025-07-31','2025-09-18','Returned','2025-08-01'),(21,5,104,NULL,'2025-07-31','2025-09-18','Returned','2025-08-01'),(22,5,104,NULL,'2025-07-31','2025-09-18','Returned','2025-08-01'),(23,5,104,NULL,'2025-07-31','2025-09-18','Returned','2025-08-01'),(24,2,101,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(25,2,101,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(26,2,101,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(27,2,101,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(28,2,101,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(29,4,102,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(30,4,102,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(31,4,102,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(32,5,103,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(33,4,102,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(34,2,101,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(35,1,108,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(36,1,109,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(37,2,110,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(38,2,110,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(39,3,106,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(40,3,107,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(41,3,106,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(42,3,107,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(43,4,112,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(44,4,111,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(45,5,104,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(46,3,107,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(47,3,106,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(48,3,107,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(49,1,108,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(50,1,109,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(51,5,104,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(52,5,103,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(53,5,104,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(54,3,106,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(55,1,108,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(56,1,109,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(57,1,108,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(58,1,109,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(59,1,109,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(60,3,107,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(61,5,103,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(62,5,103,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(63,5,104,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(64,5,104,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(65,5,104,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(66,5,104,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(67,5,104,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(68,3,106,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(69,3,107,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(70,3,106,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(71,3,107,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(72,3,106,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(73,1,108,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(74,2,105,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(75,3,106,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(76,4,102,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(77,5,104,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(78,1,108,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(79,3,106,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(80,3,106,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(81,1,108,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(82,1,109,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(83,1,108,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(84,1,108,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(85,2,105,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(86,3,107,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(87,4,102,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(88,5,104,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(89,1,108,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(90,5,104,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(91,1,109,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(92,1,108,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(93,1,108,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(94,4,102,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(95,2,101,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(96,2,101,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(97,1,108,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(98,1,108,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(99,1,108,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(100,2,105,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(101,1,109,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(102,1,108,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(103,2,105,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(104,4,112,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(105,2,110,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(106,3,107,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01'),(107,1,108,NULL,'2025-08-02','2025-09-20','Returned','2025-08-02'),(108,1,108,NULL,'2025-08-02','2025-09-20','Returned','2025-08-05'),(109,1,109,NULL,'2025-08-02','2025-09-20','Returned','2025-08-05'),(110,2,101,NULL,'2025-08-02','2025-09-20','Returned','2025-08-05'),(111,2,105,NULL,'2025-08-02','2025-09-20','Returned','2025-08-05'),(112,2,110,NULL,'2025-08-02','2025-09-20','Returned','2025-08-04'),(113,3,106,NULL,'2025-08-02','2025-09-20','Returned','2025-08-02'),(114,3,107,NULL,'2025-08-02','2025-09-20','Returned','2025-08-02'),(115,4,102,NULL,'2025-08-02','2025-09-20','Returned','2025-08-05'),(116,5,104,NULL,'2025-08-02','2025-09-20','Returned','2025-08-05'),(117,4,111,NULL,'2025-08-02','2025-09-20','Returned','2025-08-05'),(118,4,112,NULL,'2025-08-02','2025-09-20','Returned','2025-08-05'),(119,5,103,NULL,'2025-08-02','2025-09-20','Returned','2025-08-05'),(120,3,106,NULL,'2025-08-02','2025-09-20','Returned','2025-08-02'),(121,3,106,NULL,'2025-08-02','2025-09-20','Returned','2025-08-05'),(122,3,107,NULL,'2025-08-02','2025-09-20','Returned','2025-08-02'),(123,3,107,NULL,'2025-08-02','2025-09-20','Returned','2025-08-05'),(124,2,110,NULL,'2025-08-04','2025-09-22','Returned','2025-08-05'),(125,3,106,NULL,'2025-08-05','2025-09-23','Returned','2025-08-05'),(126,1,108,NULL,'2025-08-05','2025-09-23','Borrowed',NULL),(127,1,109,NULL,'2025-08-05','2025-09-23','Borrowed',NULL),(128,2,101,NULL,'2025-08-05','2025-09-23','Returned','2025-08-05'),(129,2,101,NULL,'2025-08-05','2025-09-23','Borrowed',NULL),(130,2,105,NULL,'2025-08-05','2025-09-23','Borrowed',NULL),(131,2,110,NULL,'2025-08-05','2025-09-23','Borrowed',NULL),(132,3,100,NULL,'2025-08-05','2025-09-23','Returned','2025-08-05'),(133,3,106,NULL,'2025-08-05','2025-09-23','Returned','2025-08-05'),(134,3,107,NULL,'2025-08-05','2025-09-23','Returned','2025-08-05'),(135,3,100,NULL,'2025-08-05','2025-09-23','Returned','2025-08-05'),(136,3,107,NULL,'2025-08-05','2025-09-23','Borrowed',NULL),(137,3,100,NULL,'2025-08-05','2025-09-23','Borrowed',NULL),(138,4,102,NULL,'2025-08-05','2025-09-23','Returned','2025-08-05'),(139,4,102,NULL,'2025-08-05','2025-09-23','Borrowed',NULL),(140,4,111,NULL,'2025-08-05','2025-09-23','Borrowed',NULL),(141,4,112,NULL,'2025-08-05','2025-09-23','Borrowed',NULL),(142,5,104,NULL,'2025-08-05','2025-09-23','Returned','2025-08-05'),(143,5,103,NULL,'2025-08-05','2025-09-23','Returned','2025-08-05'),(144,5,104,NULL,'2025-08-05','2025-09-23','Borrowed',NULL),(145,3,106,NULL,'2025-08-05','2025-09-23','Borrowed',NULL),(146,5,103,NULL,'2025-08-05','2025-09-23','Borrowed',NULL);
/*!40000 ALTER TABLE `transaction` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-08-05 21:39:30
