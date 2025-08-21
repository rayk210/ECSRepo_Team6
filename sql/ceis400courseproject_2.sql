CREATE DATABASE  IF NOT EXISTS `ceis400courseproject` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `ceis400courseproject`;
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
INSERT INTO `equipment` VALUES (100,'Hammer','Good','Carpenter','Loaned'),(101,'Paint Brush','Good','Painter','Available'),(102,'Torch','Good','Welder','Loaned'),(103,'Saw','Good','Plumber','Loaned'),(104,'Hacksaw','Good','Plumber','Loaned'),(105,'Paint Roller','Good','Painter','Loaned'),(106,'Chisel','Good','Carpenter','Loaned'),(107,'Screwdriver Set','Good','Carpenter','Loaned'),(108,'Voltage Tester','Good','Electrician','Loaned'),(109,'Wire Stripper','Good','Electrician','Loaned'),(110,'Heat gun','Good','Painter','Loaned'),(111,'Welding Helmet','Good','Welder','Loaned'),(112,'Gloves','Good','Welder','Available');
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
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order`
--

LOCK TABLES `order` WRITE;
/*!40000 ALTER TABLE `order` DISABLE KEYS */;
INSERT INTO `order` VALUES (1,1,100,'2025-07-15',NULL,'Cancelled'),(2,2,101,'2025-07-16',NULL,'Cancelled'),(3,3,102,'2025-07-17',NULL,'Cancelled'),(4,4,103,'2025-07-18',NULL,'Cancelled'),(5,5,104,'2025-07-19',NULL,'Cancelled'),(6,1,108,'2025-08-05',NULL,'Cancelled'),(7,1,109,'2025-08-05',NULL,'Cancelled'),(8,3,100,'2025-08-05',NULL,'Cancelled'),(9,3,106,'2025-08-05',NULL,'Cancelled'),(10,3,107,'2025-08-05',NULL,'Cancelled'),(11,3,107,'2025-08-05',NULL,'Cancelled'),(12,3,100,'2025-08-05',NULL,'Cancelled'),(13,3,106,'2025-08-05',NULL,'Cancelled'),(14,3,106,'2025-08-05',NULL,'Cancelled'),(15,5,103,'2025-08-05',NULL,'Cancelled'),(16,3,100,'2025-08-07',NULL,'Cancelled'),(17,3,100,'2025-08-08',NULL,'Cancelled'),(18,4,111,'2025-08-11',NULL,'Cancelled'),(19,3,100,'2025-08-12',NULL,'Cancelled'),(20,3,100,'2025-08-12',NULL,'Cancelled'),(21,3,100,'2025-08-12',NULL,'Cancelled'),(22,3,100,'2025-08-16',NULL,'Cancelled'),(23,3,100,'2025-08-16',NULL,'Cancelled'),(24,4,112,'2025-08-17',NULL,'Cancelled'),(25,3,107,'2025-08-20',NULL,'Cancelled'),(26,2,101,'2025-08-20',NULL,'Cancelled');
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
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reminder`
--

LOCK TABLES `reminder` WRITE;
/*!40000 ALTER TABLE `reminder` DISABLE KEYS */;
INSERT INTO `reminder` VALUES (1,3,161,'2025-08-12','Raymond should return: Screwdriver Set by 2025-08-12'),(2,3,162,'2025-08-12','Raymond has an overdue item: Chisel. Due on: 2025-08-10'),(3,3,167,'2025-08-12','No action needed for: Hammer. Time left to return: 46 days.'),(4,1,126,'2025-08-19','Jorge has an overdue item: Voltage Tester. Due on: 2025-08-11'),(5,1,127,'2025-08-19','No action needed for: Wire Stripper. Time left to return: 35 days.'),(6,2,129,'2025-08-20','No action needed for: Paint Brush. Time left to return: 34 days.'),(7,2,130,'2025-08-21','Zachary has an overdue item: Paint Roller. Due on: 2025-08-13'),(8,2,131,'2025-08-21','Zachary has an overdue item: Heat gun. Due on: 2025-08-12'),(9,4,139,'2025-08-20','Megan has an overdue item: Torch. Due on: 2025-08-12'),(10,4,140,'2025-08-11','No action needed for: Welding Helmet. Time left to return: 43 days.'),(11,4,141,'2025-08-17','No action needed for: Gloves. Time left to return: 37 days.'),(12,5,144,'2025-08-12','David should return: Hacksaw by 2025-08-12'),(13,5,146,'2025-08-21','No action needed for: Saw. Time left to return: 33 days.'),(14,4,168,'2025-08-20','Megan has an overdue item: Welding Helmet. Due on: 2025-08-13'),(15,3,169,'2025-08-12','Raymond has an overdue item: Chisel. Due on: 2025-08-11'),(16,3,170,'2025-08-12','No action needed for: Hammer. Time left to return: 49 days.'),(17,5,171,'2025-08-21','David has an overdue item: Hacksaw. Due on: 2025-08-13'),(18,3,172,'2025-08-12','No action needed for: Screwdriver Set. Time left to return: 49 days.'),(19,3,173,'2025-08-12','No action needed for: Hammer. Time left to return: 49 days.'),(20,3,174,'2025-08-12','No action needed for: Hammer. Time left to return: 49 days.'),(21,3,175,'2025-08-12','No action needed for: Chisel. Time left to return: 49 days.'),(22,3,176,'2025-08-12','No action needed for: Screwdriver Set. Time left to return: 49 days.'),(23,3,177,'2025-08-21','Raymond has an overdue item: Chisel. Due on: 2025-08-12'),(24,3,178,'2025-08-13','No action needed for: Hammer. Time left to return: 48 days.'),(25,3,179,'2025-08-13','No action needed for: Screwdriver Set. Time left to return: 49 days.'),(26,3,180,'2025-08-13','No action needed for: Screwdriver Set. Time left to return: 49 days.'),(27,3,181,'2025-08-15','No action needed for: Hammer. Time left to return: 47 days.'),(28,3,184,'2025-08-19','No action needed for: Screwdriver Set. Time left to return: 43 days.'),(29,3,208,'2025-08-21','No action needed for: Hammer. Time left to return: 47 days.'),(30,1,209,'2025-08-21','No action needed for: Voltage Tester. Time left to return: 47 days.'),(31,3,265,'2025-08-20','No action needed for: Screwdriver Set. Time left to return: 49 days.'),(32,3,266,'2025-08-20','No action needed for: Screwdriver Set. Time left to return: 49 days.'),(33,1,276,'2025-08-21','No action needed for: Wire Stripper. Time left to return: 48 days.'),(34,3,277,'2025-08-21','Raymond should return: Screwdriver Set by 2025-08-21');
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
  `returnCondition` enum('Good','Damaged','Lost') DEFAULT NULL,
  PRIMARY KEY (`transactionID`),
  KEY `empID` (`empID`),
  KEY `orderID` (`orderID`),
  KEY `transaction_ibfk_2` (`equipmentID`),
  CONSTRAINT `transaction_ibfk_1` FOREIGN KEY (`empID`) REFERENCES `employee` (`empID`),
  CONSTRAINT `transaction_ibfk_2` FOREIGN KEY (`equipmentID`) REFERENCES `equipment` (`equipmentID`),
  CONSTRAINT `transaction_ibfk_3` FOREIGN KEY (`orderID`) REFERENCES `order` (`orderID`)
) ENGINE=InnoDB AUTO_INCREMENT=278 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction`
--

LOCK TABLES `transaction` WRITE;
/*!40000 ALTER TABLE `transaction` DISABLE KEYS */;
INSERT INTO `transaction` VALUES (13,1,102,NULL,'2025-07-31','2025-08-07','Returned','2025-08-01',NULL),(14,2,103,NULL,'2025-07-31','2025-08-05','Returned','2025-08-01',NULL),(15,3,104,NULL,'2025-07-31','2025-08-03','Returned','2025-08-01',NULL),(16,5,103,NULL,'2025-07-31','2025-09-18','Returned','2025-08-01',NULL),(17,4,102,NULL,'2025-07-31','2025-09-18','Returned','2025-08-01',NULL),(18,5,104,NULL,'2025-07-31','2025-09-18','Returned','2025-08-01',NULL),(19,5,104,NULL,'2025-07-31','2025-09-18','Returned','2025-08-01',NULL),(20,5,104,NULL,'2025-07-31','2025-09-18','Returned','2025-08-01',NULL),(21,5,104,NULL,'2025-07-31','2025-09-18','Returned','2025-08-01',NULL),(22,5,104,NULL,'2025-07-31','2025-09-18','Returned','2025-08-01',NULL),(23,5,104,NULL,'2025-07-31','2025-09-18','Returned','2025-08-01',NULL),(24,2,101,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(25,2,101,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(26,2,101,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(27,2,101,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(28,2,101,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(29,4,102,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(30,4,102,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(31,4,102,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(32,5,103,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(33,4,102,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(34,2,101,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(35,1,108,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(36,1,109,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(37,2,110,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(38,2,110,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(39,3,106,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(40,3,107,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(41,3,106,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(42,3,107,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(43,4,112,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(44,4,111,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(45,5,104,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(46,3,107,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(47,3,106,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(48,3,107,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(49,1,108,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(50,1,109,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(51,5,104,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(52,5,103,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(53,5,104,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(54,3,106,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(55,1,108,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(56,1,109,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(57,1,108,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(58,1,109,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(59,1,109,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(60,3,107,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(61,5,103,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(62,5,103,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(63,5,104,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(64,5,104,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(65,5,104,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(66,5,104,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(67,5,104,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(68,3,106,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(69,3,107,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(70,3,106,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(71,3,107,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(72,3,106,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(73,1,108,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(74,2,105,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(75,3,106,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(76,4,102,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(77,5,104,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(78,1,108,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(79,3,106,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(80,3,106,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(81,1,108,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(82,1,109,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(83,1,108,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(84,1,108,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(85,2,105,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(86,3,107,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(87,4,102,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(88,5,104,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(89,1,108,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(90,5,104,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(91,1,109,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(92,1,108,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(93,1,108,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(94,4,102,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(95,2,101,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(96,2,101,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(97,1,108,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(98,1,108,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(99,1,108,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(100,2,105,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(101,1,109,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(102,1,108,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(103,2,105,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(104,4,112,NULL,'2025-08-01','2025-08-13','Returned','2025-08-01',NULL),(105,2,110,NULL,'2025-08-01','2025-08-13','Returned','2025-08-01',NULL),(106,3,107,NULL,'2025-08-01','2025-09-19','Returned','2025-08-01',NULL),(107,1,108,NULL,'2025-08-02','2025-09-20','Returned','2025-08-02',NULL),(108,1,108,NULL,'2025-08-02','2025-09-20','Returned','2025-08-05',NULL),(109,1,109,NULL,'2025-08-02','2025-09-20','Returned','2025-08-05',NULL),(110,2,101,NULL,'2025-08-02','2025-09-20','Returned','2025-08-05',NULL),(111,2,105,NULL,'2025-08-02','2025-08-13','Returned','2025-08-05',NULL),(112,2,110,NULL,'2025-08-02','2025-09-20','Returned','2025-08-04',NULL),(113,3,106,NULL,'2025-08-02','2025-09-20','Returned','2025-08-02',NULL),(114,3,107,NULL,'2025-08-02','2025-09-20','Returned','2025-08-02',NULL),(115,4,102,NULL,'2025-08-02','2025-09-20','Returned','2025-08-05',NULL),(116,5,104,NULL,'2025-08-02','2025-09-20','Returned','2025-08-05',NULL),(117,4,111,NULL,'2025-08-02','2025-09-20','Returned','2025-08-05',NULL),(118,4,112,NULL,'2025-08-02','2025-09-20','Returned','2025-08-05',NULL),(119,5,103,NULL,'2025-08-02','2025-09-20','Returned','2025-08-05',NULL),(120,3,106,NULL,'2025-08-02','2025-09-20','Returned','2025-08-02',NULL),(121,3,106,NULL,'2025-08-02','2025-09-20','Returned','2025-08-05',NULL),(122,3,107,NULL,'2025-08-02','2025-09-20','Returned','2025-08-02',NULL),(123,3,107,NULL,'2025-08-02','2025-09-20','Returned','2025-08-05',NULL),(124,2,110,NULL,'2025-08-04','2025-09-22','Returned','2025-08-05',NULL),(125,3,106,NULL,'2025-08-05','2025-09-23','Returned','2025-08-05',NULL),(126,1,108,NULL,'2025-08-05','2025-08-11','Returned','2025-08-19',NULL),(127,1,109,NULL,'2025-08-05','2025-09-23','Returned','2025-08-19',NULL),(128,2,101,NULL,'2025-08-05','2025-09-23','Returned','2025-08-05',NULL),(129,2,101,NULL,'2025-08-05','2025-09-23','Returned','2025-08-20','Good'),(130,2,105,NULL,'2025-08-05','2025-08-13','Borrowed',NULL,NULL),(131,2,110,NULL,'2025-08-05','2025-08-12','Borrowed',NULL,NULL),(132,3,100,NULL,'2025-08-05','2025-09-23','Returned','2025-08-05',NULL),(133,3,106,NULL,'2025-08-05','2025-09-23','Returned','2025-08-05',NULL),(134,3,107,NULL,'2025-08-05','2025-09-23','Returned','2025-08-05',NULL),(135,3,100,NULL,'2025-08-05','2025-09-23','Returned','2025-08-05',NULL),(136,3,107,NULL,'2025-08-05','2025-09-23','Returned','2025-08-08',NULL),(137,3,100,NULL,'2025-08-05','2025-09-23','Returned','2025-08-07',NULL),(138,4,102,NULL,'2025-08-05','2025-09-23','Returned','2025-08-05',NULL),(139,4,102,NULL,'2025-08-05','2025-08-12','Borrowed',NULL,NULL),(140,4,111,NULL,'2025-08-05','2025-09-23','Returned','2025-08-11',NULL),(141,4,112,NULL,'2025-08-05','2025-09-23','Returned','2025-08-17',NULL),(142,5,104,NULL,'2025-08-05','2025-09-23','Returned','2025-08-05',NULL),(143,5,103,NULL,'2025-08-05','2025-09-23','Returned','2025-08-05',NULL),(144,5,104,NULL,'2025-08-05','2025-08-12','Returned','2025-08-12',NULL),(145,3,106,NULL,'2025-08-05','2025-09-23','Returned','2025-08-08',NULL),(146,5,103,NULL,'2025-08-05','2025-09-23','Borrowed',NULL,NULL),(147,3,100,NULL,'2025-08-07','2025-09-25','Returned','2025-08-08',NULL),(148,3,106,NULL,'2025-08-08','2025-09-26','Returned','2025-08-08',NULL),(149,3,100,NULL,'2025-08-08','2025-09-26','Returned','2025-08-08',NULL),(150,3,106,NULL,'2025-08-08','2025-09-26','Returned','2025-08-08',NULL),(151,3,107,NULL,'2025-08-08','2025-09-26','Returned','2025-08-08',NULL),(152,3,100,NULL,'2025-08-08','2025-09-26','Returned','2025-08-08',NULL),(153,3,107,NULL,'2025-08-08','2025-09-26','Returned','2025-08-08',NULL),(154,3,106,NULL,'2025-08-08','2025-09-26','Returned','2025-08-08',NULL),(155,3,107,NULL,'2025-08-08','2025-09-26','Returned','2025-08-09',NULL),(156,3,100,NULL,'2025-08-08','2025-09-26','Returned','2025-08-08',NULL),(157,3,106,NULL,'2025-08-08','2025-09-26','Returned','2025-08-09',NULL),(158,3,100,NULL,'2025-08-08','2025-09-26','Returned','2025-08-08',NULL),(159,3,100,NULL,'2025-08-08','2025-09-26','Returned','2025-08-09',NULL),(160,3,106,NULL,'2025-08-09','2025-09-27','Returned','2025-08-09',NULL),(161,3,107,NULL,'2025-08-09','2025-08-12','Returned','2025-08-12',NULL),(162,3,106,NULL,'2025-08-09','2025-08-10','Returned','2025-08-12',NULL),(163,3,100,NULL,'2025-08-09','2025-09-27','Returned','2025-08-09',NULL),(164,3,100,NULL,'2025-08-09','2025-09-27','Returned','2025-08-09',NULL),(165,3,100,NULL,'2025-08-09','2025-09-27','Returned','2025-08-09',NULL),(166,3,100,NULL,'2025-08-09','2025-09-27','Returned','2025-08-09',NULL),(167,3,100,NULL,'2025-08-09','2025-09-27','Returned','2025-08-12',NULL),(168,4,111,NULL,'2025-08-11','2025-08-13','Borrowed',NULL,NULL),(169,3,106,NULL,'2025-08-12','2025-08-11','Returned','2025-08-12',NULL),(170,3,100,NULL,'2025-08-12','2025-09-30','Returned','2025-08-12',NULL),(171,5,104,NULL,'2025-08-12','2025-08-13','Borrowed',NULL,NULL),(172,3,107,NULL,'2025-08-12','2025-09-30','Returned','2025-08-12',NULL),(173,3,100,NULL,'2025-08-12','2025-09-30','Returned','2025-08-12',NULL),(174,3,100,NULL,'2025-08-12','2025-09-30','Returned','2025-08-12',NULL),(175,3,106,NULL,'2025-08-12','2025-09-30','Returned','2025-08-12',NULL),(176,3,107,NULL,'2025-08-12','2025-09-30','Returned','2025-08-12',NULL),(177,3,106,NULL,'2025-08-12','2025-08-12','Borrowed',NULL,NULL),(178,3,100,NULL,'2025-08-12','2025-09-30','Returned','2025-08-13',NULL),(179,3,107,NULL,'2025-08-13','2025-10-01','Returned','2025-08-13',NULL),(180,3,107,NULL,'2025-08-13','2025-10-01','Returned','2025-08-13',NULL),(181,3,100,NULL,'2025-08-13','2025-10-01','Returned','2025-08-15',NULL),(183,3,107,NULL,'2025-08-13','2025-10-01','Returned','2025-08-13',NULL),(184,3,107,NULL,'2025-08-13','2025-10-01','Returned','2025-08-19','Damaged'),(185,3,100,NULL,'2025-08-16','2025-10-04','Returned','2025-08-16',NULL),(186,3,100,NULL,'2025-08-16','2025-10-04','Returned','2025-08-16',NULL),(187,3,100,NULL,'2025-08-16','2025-10-04','Returned','2025-08-16',NULL),(188,3,100,NULL,'2025-08-16','2025-10-04','Returned','2025-08-16',NULL),(189,3,100,NULL,'2025-08-16','2025-10-04','Returned','2025-08-16',NULL),(190,3,100,NULL,'2025-08-16','2025-10-04','Returned','2025-08-16',NULL),(191,3,100,NULL,'2025-08-16','2025-10-04','Returned','2025-08-16',NULL),(192,3,100,NULL,'2025-08-16','2025-10-04','Returned','2025-08-16',NULL),(193,3,100,NULL,'2025-08-16','2025-10-04','Returned','2025-08-16',NULL),(194,3,100,NULL,'2025-08-16','2025-10-04','Returned','2025-08-16',NULL),(195,3,100,NULL,'2025-08-16','2025-10-04','Returned','2025-08-16',NULL),(196,3,100,NULL,'2025-08-16','2025-10-04','Returned','2025-08-16',NULL),(197,3,100,NULL,'2025-08-16','2025-10-04','Returned','2025-08-16',NULL),(198,3,100,NULL,'2025-08-16','2025-10-04','Returned','2025-08-16',NULL),(199,3,100,NULL,'2025-08-16','2025-10-04','Returned','2025-08-16',NULL),(200,3,100,NULL,'2025-08-16','2025-10-04','Returned','2025-08-16',NULL),(201,3,100,NULL,'2025-08-16','2025-10-04','Returned','2025-08-16',NULL),(202,3,100,NULL,'2025-08-16','2025-10-04','Returned','2025-08-16',NULL),(203,3,100,NULL,'2025-08-16','2025-10-04','Returned','2025-08-16',NULL),(204,3,100,NULL,'2025-08-16','2025-10-04','Returned','2025-08-16',NULL),(205,3,100,NULL,'2025-08-16','2025-10-04','Returned','2025-08-16',NULL),(206,3,100,NULL,'2025-08-16','2025-10-04','Returned','2025-08-16',NULL),(207,3,100,NULL,'2025-08-16','2025-10-04','Returned','2025-08-16',NULL),(208,3,100,NULL,'2025-08-19','2025-10-07','Borrowed',NULL,NULL),(209,1,108,NULL,'2025-08-19','2025-10-07','Borrowed',NULL,NULL),(210,1,109,NULL,'2025-08-19','2025-10-07','Returned','2025-08-19',NULL),(211,1,109,NULL,'2025-08-19','2025-10-07','Returned','2025-08-19',NULL),(212,1,109,NULL,'2025-08-19','2025-10-07','Returned','2025-08-19',NULL),(213,1,109,NULL,'2025-08-19','2025-10-07','Returned','2025-08-19',NULL),(214,1,109,NULL,'2025-08-19','2025-10-07','Returned','2025-08-19',NULL),(215,1,109,NULL,'2025-08-19','2025-10-07','Returned','2025-08-19','Damaged'),(216,1,109,NULL,'2025-08-19','2025-10-07','Returned','2025-08-19','Damaged'),(217,1,109,NULL,'2025-08-19','2025-10-07','Returned','2025-08-19','Damaged'),(218,1,109,NULL,'2025-08-19','2025-10-07','Returned','2025-08-19','Damaged'),(219,1,109,NULL,'2025-08-19','2025-10-07','Returned','2025-08-19','Damaged'),(220,1,109,NULL,'2025-08-19','2025-10-07','Returned','2025-08-19','Damaged'),(221,3,107,NULL,'2025-08-19','2025-10-07','Returned','2025-08-20','Damaged'),(222,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Damaged'),(223,1,109,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Damaged'),(224,1,109,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Good'),(225,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Damaged'),(226,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Good'),(227,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Damaged'),(228,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Good'),(229,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Damaged'),(230,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Good'),(231,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Damaged'),(232,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Damaged'),(233,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Damaged'),(234,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Damaged'),(235,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Damaged'),(236,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Damaged'),(237,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Good'),(238,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Damaged'),(239,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Damaged'),(240,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Damaged'),(241,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Damaged'),(242,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Good'),(243,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Good'),(244,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Damaged'),(245,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Good'),(246,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Good'),(247,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Damaged'),(248,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Damaged'),(249,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Good'),(250,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Damaged'),(251,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Damaged'),(252,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Damaged'),(253,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Good'),(254,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Good'),(255,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Damaged'),(256,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Damaged'),(257,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Good'),(258,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Good'),(259,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Damaged'),(260,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Good'),(261,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Good'),(262,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Damaged'),(263,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Damaged'),(264,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Good'),(265,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Good'),(266,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Good'),(267,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Damaged'),(268,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Good'),(269,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Good'),(270,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Damaged'),(271,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Good'),(272,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Damaged'),(273,3,107,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Good'),(274,1,109,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Damaged'),(275,1,109,NULL,'2025-08-20','2025-10-08','Returned','2025-08-20','Good'),(276,1,109,NULL,'2025-08-20','2025-10-08','Borrowed',NULL,NULL),(277,3,107,NULL,'2025-08-20','2025-08-21','Borrowed',NULL,NULL);
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

-- Dump completed on 2025-08-21 17:25:01
