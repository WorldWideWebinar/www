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
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `uid` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(320) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `language` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT 'eng',
  `profile_image` varchar(2048) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uid_UNIQUE` (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'ys','연수','ys@naver.com','$2a$10$REE1aGvec7EsI6Kcp520uOreKiGr49uAy0tJsGGu52FlrfQItGvDu','en','https://i11a501.p.ssafy.io/api/images/default_profile.png','2024-08-14 09:13:56','2024-08-14 09:13:56'),(2,'sb','수빈','sb@naver.com','$2a$10$Uo.tRT8GZ.Ncz1CMdQCmyuhxjOl3XFeNb5cXlO1Er6n9mzqxd.3F2','en','https://i11a501.p.ssafy.io/api/images/default_profile.png','2024-08-14 09:14:34','2024-08-14 09:14:34'),(3,'jy','주영','jy@naver.com','$2a$10$YsQv.z7dxSxWnz61HuL.JuLafFnvp5JN.uENI4.F2V5PyCTa.sOlq','en','https://i11a501.p.ssafy.io/api/images/837cf1c9-f0d5-4b37-9e05-cbc4c190341c.png','2024-08-14 09:15:45','2024-08-16 00:22:30'),(4,'sj','선재','sss@naver.com','$2a$10$ckhxgyLAIoeJL3DRMeb8M.iyfUNNSUYrCjMYyzBzyVZrGwOrmgJlS','en','https://i11a501.p.ssafy.io/api/images/cfc37ebe-22b1-4b6c-a43b-497d57417b2d.png','2024-08-14 09:16:12','2024-08-16 00:23:39'),(5,'park','park','park@naver.com','$2a$10$AiqyVvCFOC9dU6uTzSWWpO/lXOJLngQrZ1UJ3LucbudXN8d0VUe5W','en','https://i11a501.p.ssafy.io/api/images/fcb07d83-a261-4bed-9850-6fe4f8f202b6.png','2024-08-14 09:16:42','2024-08-16 00:24:34'),(6,'yyyy','용수','yyyy@naver.com','$2a$10$welkaENVvZGNua.6lFXfnOkye6klsipx8WcG8MkghopT4hgakNKqC','ja','https://i11a501.p.ssafy.io/api/images/default_profile.png','2024-08-14 09:17:36','2024-08-14 09:17:36'),(7,'admin','관리자','admin@naver.com','$2a$10$QrURicPHASekK7MR6c822uYVzRaJ4Qr4zKzQAMY1OFs8bIZSFg2WG','ko','https://i11a501.p.ssafy.io/api/images/default_profile.png','2024-08-14 09:18:07','2024-08-14 09:18:07'),(8,'ssafy','김싸피','ssafy@naver.com','$2a$10$n2wlk1o1a8wZaeCK.vLRw.L9KD.EBhNG4TvSWQ0T9SWbPxusqwDBG','en','https://i11a501.p.ssafy.io/api/images/default_profile.png','2024-08-14 09:18:33','2024-08-14 09:18:33'),(9,'hi','hi','hi@naver.com','$2a$10$VD8.7zt0HFe91tQ0kNQuKevEis0egzpHqVUwiOSE2Dgn/FiIvPfeS','ko','https://i11a501.p.ssafy.io/api/images/default_profile.png','2024-08-14 12:59:48','2024-08-14 13:10:36'),(10,'cookie','cookie','cookie@naver.com','$2a$10$Id3ZKaluhE7HsQlyYzVLIOlUiXweqxCSsvtYN6E7rbze8.YYyeWqm','en','https://i11a501.p.ssafy.io/api/images/15e6f1b0-93a0-494e-8faa-c834c82cb8f5.jpg','2024-08-14 13:05:46','2024-08-14 13:12:00'),(11,'raypark1241','박준영','raypark1241@gmail.com','$2a$10$faiy5yrVJY7kfWzhWPw9H.aKN/cZe/TZmacRUvoK1/SA8FZL5IFri','en','https://i11a501.p.ssafy.io/api/images/bee96ec8-de5c-41be-8e24-be6e4b663492.jpeg','2024-08-15 04:11:35','2024-08-15 04:12:58'),(12,'mmmm','mmmm','mmmm@mmmm.com','$2a$10$F15xS7owolPLDNJUjQBSBuQu7sR1EYToihcYcME4QvST.8dxTk2tK','ko','https://i11a501.p.ssafy.io/api/images/default_profile.png','2024-08-15 18:09:47','2024-08-15 18:09:47');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
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
