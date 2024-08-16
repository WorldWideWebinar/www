-- MySQL dump 10.13  Distrib 8.0.38, for Win64 (x86_64)
--
-- Host: i11a501.p.ssafy.io    Database: globalcc
-- ------------------------------------------------------
-- Server version	8.0.32

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
-- Table structure for table `user_meeting`
--

DROP TABLE IF EXISTS `user_meeting`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_meeting` (
  `user_meeting_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `meeting_id` int NOT NULL,
  `attend` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`user_meeting_id`),
  KEY `user_id` (`user_id`),
  KEY `user_meeting_ibfk_2` (`meeting_id`),
  CONSTRAINT `user_meeting_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
  CONSTRAINT `user_meeting_ibfk_2` FOREIGN KEY (`meeting_id`) REFERENCES `meeting` (`meeting_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=69 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_meeting`
--

LOCK TABLES `user_meeting` WRITE;
/*!40000 ALTER TABLE `user_meeting` DISABLE KEYS */;
INSERT INTO `user_meeting` VALUES (1,1,1,1),(2,2,1,1),(3,3,1,1),(4,6,2,1),(5,5,2,1),(6,4,2,1),(7,1,3,1),(8,2,3,1),(9,3,3,1),(10,4,3,1),(11,5,3,1),(12,6,3,1),(13,1,4,NULL),(14,2,4,NULL),(15,3,4,NULL),(31,3,8,NULL),(32,5,8,NULL),(33,2,8,NULL),(34,4,8,NULL),(35,1,8,NULL),(36,3,9,NULL),(37,5,9,NULL),(38,2,9,NULL),(39,4,9,NULL),(40,1,9,NULL),(41,1,10,NULL),(42,2,10,NULL),(43,3,10,NULL),(44,4,10,NULL),(45,5,10,NULL),(46,6,10,NULL),(47,3,11,NULL),(48,5,11,NULL),(49,2,11,NULL),(50,4,11,NULL),(51,1,11,NULL),(57,1,13,NULL),(58,2,13,NULL),(59,3,13,NULL),(60,4,13,NULL),(61,5,13,NULL),(62,6,13,NULL),(63,1,14,NULL),(64,2,14,NULL),(65,3,14,NULL),(66,4,14,NULL),(67,5,14,NULL),(68,6,14,NULL);
/*!40000 ALTER TABLE `user_meeting` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-08-16  9:30:03
