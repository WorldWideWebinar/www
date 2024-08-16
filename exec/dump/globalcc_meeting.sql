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
-- Table structure for table `meeting`
--

DROP TABLE IF EXISTS `meeting`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `meeting` (
  `meeting_id` int NOT NULL AUTO_INCREMENT,
  `team_id` int NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `start_at` timestamp NULL DEFAULT NULL,
  `end_at` timestamp NULL DEFAULT NULL,
  `content` text COLLATE utf8mb4_unicode_ci,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `session_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `detail` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`meeting_id`),
  KEY `team_id` (`team_id`),
  CONSTRAINT `meeting_ibfk_1` FOREIGN KEY (`team_id`) REFERENCES `team` (`team_id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `meeting`
--

LOCK TABLES `meeting` WRITE;
/*!40000 ALTER TABLE `meeting` DISABLE KEYS */;
INSERT INTO `meeting` VALUES (1,1,'백엔드 회의','2024-08-16 00:00:00','2024-08-14 07:00:00','백엔드 팀 회의입니다.','2024-08-14 09:19:31','2024-08-14 09:19:31',NULL,'Backend project discussion'),(2,2,'프론트엔드 회의','2024-08-16 00:00:00','2024-08-16 07:00:00','프론트엔드 팀 회의입니다.','2024-08-14 09:19:31','2024-08-14 09:19:31',NULL,'Frontend project discussion'),(3,3,'글로벌 CC 회의','2024-08-16 00:00:00','2024-08-16 07:00:00','글로벌 CC 팀 회의입니다.','2024-08-14 09:19:31','2024-08-16 00:16:56',NULL,'GlobalCC project discussion'),(4,1,'아키텍처','2024-08-13 18:00:00','2024-08-14 01:00:00',NULL,'2024-08-14 09:21:28','2024-08-14 09:21:28',NULL,'기술 스택 관련'),(8,5,'Mobility-Conference','2024-08-14 04:00:00','2024-08-14 05:00:00',NULL,'2024-08-14 13:32:44','2024-08-14 13:33:23',NULL,'Having Conference for BM'),(9,5,'Considering-BM','2024-08-14 18:00:00','2024-08-14 20:00:00',NULL,'2024-08-14 13:37:18','2024-08-15 03:13:45',NULL,'Everyone Coming'),(10,3,'글로벌-CC-정기-회의','2024-08-14 18:10:00','2024-08-14 19:00:00',NULL,'2024-08-15 03:00:42','2024-08-15 03:00:42',NULL,'8-15 정기 회의'),(11,5,'Operation','2024-08-15 18:00:00','2024-08-15 20:00:00',NULL,'2024-08-15 03:13:01','2024-08-15 03:13:01',NULL,'Openration Method '),(13,3,'글로벌-CC-아침회의','2024-08-15 16:00:00','2024-08-15 17:00:00',NULL,'2024-08-16 00:14:23','2024-08-16 00:14:23',NULL,'금요일 아침 회의'),(14,3,'글로벌-CC-아침회의','2024-08-18 15:00:00','2024-08-18 16:00:00',NULL,'2024-08-16 00:16:26','2024-08-16 00:16:26',NULL,'글로벌 CC 정기 아침 회의');
/*!40000 ALTER TABLE `meeting` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-08-16  9:30:02
