-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: auth_db
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `role` varchar(20) NOT NULL,
  `last_login` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin','admin123','ADMIN',NULL),(2,'chaitanya','yo','student',NULL),(3,'anubha','tough','instructor',NULL),(4,'advik','yo','student',NULL),(5,'david','qwerty','student',NULL),(6,'HAALAND','MCI','instructor',NULL),(7,'admin2','$2a$10$MFwj7./o47IvangjJ3L.mOJY79YF1HuQs3g00q2gjbMOpEUWftpj2','admin','2025-11-27 20:11:31'),(8,'dennispro','$2a$10$w8Ysff0Js/NuSSVCvvZOjOI3Y9L6NOoC6xNNMX3I/MJV57Vu1EIYq','student','2025-11-21 02:56:20'),(9,'profdavid','$2a$10$OOVNQfCedgibCWmcRW2eFu6G00l3OhxzW85ClZyaqR1.xnVjlcLt6','instructor','2025-11-27 20:12:00'),(10,'chaitanya','$2a$10$9rt59n3vJY.hdwFdvn/uzOU2UcVeTejw9ps0BdfW1ZiRsuNIbS512','student',NULL),(11,'advik','$2a$10$KlDWuqoqGcGcICmS1E1I9uGoGYgx/WMnAAgfgMo.PTlDcyEsubaWq','student',NULL),(12,'krishna','$2a$10$MeMs8c2Vj/vfTNLj/1WX2uG3dQZxf52WXtY3pIAxE53WxjpQPKdGq','student','2025-11-27 21:40:45'),(13,'aryan','$2a$10$gTxeuI7X1wiUCMLGLXYvdegdjbcZ9OivmC7T6tDTP.cH8hgE4votW','student','2025-11-27 20:12:18'),(14,'eddy24','$2a$10$jQwzCaXeJJmPGk5hIH6PD.ES/vzLpL6vxKEjN1SI7C85EXrQ3B3XO','student','2025-11-23 19:12:47'),(15,'anthony24','$2a$10$fQygajpIc0Y.eQabRxP8puWUEZXFVWCTsDE6/gqomA7phZ8HEUAh2','instructor','2025-11-25 00:24:19'),(16,'admin3','$2a$10$VUfWDCNL.JKYJNiOyam.gupl1ejzWLC/zL70r7aDcfGl2nYWfYI8a','admin',NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-27 21:55:22
