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
-- Table structure for table `user_team`
--

DROP TABLE IF EXISTS `user_team`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_team` (
  `user_group_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `team_id` int NOT NULL,
  `last_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `admission` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`user_group_id`),
  KEY `user_id` (`user_id`),
  KEY `user_team_ibfk_2` (`team_id`),
  CONSTRAINT `user_team_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
  CONSTRAINT `user_team_ibfk_2` FOREIGN KEY (`team_id`) REFERENCES `team` (`team_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_team`
--

LOCK TABLES `user_team` WRITE;
/*!40000 ALTER TABLE `user_team` DISABLE KEYS */;
INSERT INTO `user_team` VALUES (1,1,1,'2024-08-14 09:19:31',1),(2,2,1,'2024-08-14 09:19:31',1),(3,3,1,'2024-08-14 09:19:31',1),(4,6,2,'2024-08-14 09:19:31',1),(5,5,2,'2024-08-14 09:19:31',1),(6,4,2,'2024-08-14 09:19:31',1),(7,1,3,'2024-08-14 09:19:31',0),(8,2,3,'2024-08-14 09:19:31',0),(9,3,3,'2024-08-14 09:19:31',0),(10,4,3,'2024-08-14 09:19:31',0),(11,5,3,'2024-08-14 09:19:31',0),(12,6,3,'2024-08-14 09:19:31',0),(19,3,5,NULL,0),(20,5,5,NULL,0),(21,2,5,NULL,0),(22,4,5,NULL,0),(23,1,5,NULL,0),(24,3,6,NULL,0),(25,11,6,NULL,0),(26,3,7,NULL,0),(27,11,7,NULL,0),(28,11,8,NULL,0),(29,8,8,NULL,0);
/*!40000 ALTER TABLE `user_team` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-08-16  9:30:01
