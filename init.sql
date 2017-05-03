CREATE DATABASE  IF NOT EXISTS `terramail` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;
USE `terramail`;
-- MySQL dump 10.13  Distrib 5.7.17, for Win64 (x86_64)
--
-- Host: localhost    Database: terramail
-- ------------------------------------------------------
-- Server version	5.7.17-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `attachment_entity`
--

DROP TABLE IF EXISTS `attachment_entity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `attachment_entity` (
  `id` varchar(35) NOT NULL,
  `body` longblob,
  `file_name` varchar(255) DEFAULT NULL,
  `message_id` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attachment_entity`
--

LOCK TABLES `attachment_entity` WRITE;
/*!40000 ALTER TABLE `attachment_entity` DISABLE KEYS */;
/*!40000 ALTER TABLE `attachment_entity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `folder_entity`
--

DROP TABLE IF EXISTS `folder_entity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `folder_entity` (
  `id` varchar(35) NOT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `parent_folder_id` varchar(255) DEFAULT NULL,
  `unread_messages` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `folder_entity`
--

LOCK TABLES `folder_entity` WRITE;
/*!40000 ALTER TABLE `folder_entity` DISABLE KEYS */;
/*!40000 ALTER TABLE `folder_entity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `message_entity`
--

DROP TABLE IF EXISTS `message_entity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `message_entity` (
  `id` varchar(32) NOT NULL,
  `create_date` bigint(20) DEFAULT NULL,
  `folder_id` varchar(255) DEFAULT NULL,
  `from_address` longtext,
  `headers` longtext,
  `message_body` longtext,
  `subject` longtext,
  `to_address` longtext,
  PRIMARY KEY (`id`),
  KEY `IDXqg0ibctuceoyb7gji1ucuyd27` (`create_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `message_entity`
--

LOCK TABLES `message_entity` WRITE;
/*!40000 ALTER TABLE `message_entity` DISABLE KEYS */;
/*!40000 ALTER TABLE `message_entity` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER insert_fulltext 
AFTER INSERT ON message_entity 
FOR EACH ROW 
INSERT INTO messages_fts VALUES (NEW.id, NEW.`subject`, NEW.`message_body`) */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `messages_fts`
--

DROP TABLE IF EXISTS `messages_fts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `messages_fts` (
  `id` varchar(32) NOT NULL,
  `subject` text,
  `body` text,
  PRIMARY KEY (`id`),
  FULLTEXT KEY `subj_fts` (`subject`),
  FULLTEXT KEY `body_fts` (`body`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `messages_fts`
--

LOCK TABLES `messages_fts` WRITE;
/*!40000 ALTER TABLE `messages_fts` DISABLE KEYS */;
/*!40000 ALTER TABLE `messages_fts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'terramail'
--

--
-- Dumping routines for database 'terramail'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-05-02 16:43:45
