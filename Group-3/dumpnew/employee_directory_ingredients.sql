-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: employee_directory
-- ------------------------------------------------------
-- Server version	9.1.0

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
-- Table structure for table `ingredients`
--

DROP TABLE IF EXISTS `ingredients`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ingredients` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `icon` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=75 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ingredients`
--

LOCK TABLES `ingredients` WRITE;
/*!40000 ALTER TABLE `ingredients` DISABLE KEYS */;
INSERT INTO `ingredients` VALUES (1,'Toppings bổ sung: Bơ','fas fa-plus-circle'),(2,'Cà rốt','fas fa-carrot'),(3,'Celery Stalks','fas fa-seedling'),(4,'ức gà','fas fa-drumstick-bite'),(5,'rau mùi tây phẳng','fas fa-leaf'),(6,'tỏi','fas fa-seedling'),(7,'Dầu ô liu','fas fa-oil-can'),(8,'Cà chua đóng hộp','fas fa-box-tissue'),(9,'đậu lăng','fas fa-seedling'),(10,'muối và hạt tiêu','fas fa-cubes-stacked'),(11,'củ cải','fas fa-carrot'),(12,'Rau','fas fa-leaf'),(13,'củ hành','fas fa-seedling'),(14,'măng tây','fas fa-seedling'),(15,'Evoo','fas fa-oil-can'),(16,'đậu Hà Lan','fas fa-seedling'),(17,'Nước dùng rau','fas fa-box'),(18,'Giấm balsamic','fas fa-wine-bottle'),(19,'Kale xoăn','fas fa-leaf'),(20,'nước dùng thịt bò','fas fa-box'),(21,'cần tây','fas fa-seedling'),(22,'kem súp kem','fas fa-leaf'),(23,'Hành lá','fas fa-leaf'),(24,'Khoai tây mới','fas fa-apple-alt'),(25,'Dale\'s Drafting','fas fa-bottle-droplet'),(26,'thịt hầm','fas fa-drumstick-bite'),(27,'Nước','fas fa-tint'),(28,'Gạo nâu','fas fa-bowl-rice'),(29,'Hạt cần tây','fas fa-circle'),(30,'Đậu thận','fas fa-circle'),(31,'Marjoram','fas fa-leaf'),(32,'Thyme','fas fa-leaf'),(33,'cà tím','fas fa-eggplant'),(34,'Đậu xanh','fas fa-seedling'),(35,'tiêu đất','fas fa-circle'),(36,'Hiền nhân mặt đất','fas fa-leaf'),(37,'khói lỏng','fas fa-smog'),(38,'Chuông tiêu','fas fa-pepper-hot'),(39,'Muối biển','fas fa-cube'),(40,'Sriracha','fas fa-pepper-hot'),(41,'Cà chua','fas fa-apple-alt'),(42,'Dầu nho','fas fa-oil-can'),(43,'Dầu dừa','fas fa-oil-can'),(44,'súp lơ','fas fa-seedling'),(45,'bông cải xanh','fas fa-seedling'),(46,'Tôi là nước sốt','fas fa-bottle-droplet'),(47,'Dầu mè','fas fa-oil-can'),(48,'Hạt mè','fas fa-circle'),(49,'Tops hành lá bổ sung','fas fa-leaf'),(50,'muối','fas fa-cube'),(51,'Hạt điều','fas fa-tree'),(52,'Anh đào khô','fas fa-apple-alt'),(53,'đậu xanh khô','fas fa-circle'),(54,'Thyme khô','fas fa-leaf'),(55,'Cumin mặt đất','fas fa-mortar-pestle'),(56,'Mật ong','fas fa-leaf'),(57,'nước cam','fas fa-leaf'),(58,'quinoa','fas fa-circle'),(59,'Giấm rượu vang đỏ','fas fa-wine-bottle'),(60,'BIỂN-SAL','fas fa-cube'),(61,'Cà chua phơi nắng','fas fa-sun'),(62,'nghệ','fas fa-mortar-pestle'),(63,'Đậu đen','fas fa-circle'),(64,'Chili Powder','fas fa-mortar-pestle'),(65,'Cumin','fas fa-mortar-pestle'),(66,'Tùy chọn: Nước sốt nóng','fas fa-pepper-hot'),(67,'cơm','fas fa-bowl-rice'),(68,'lá nguyệt quế','fas fa-leaf'),(69,'Rosemary','fas fa-leaf'),(70,'Đậu','fas fa-leaf'),(71,'hạt tiêu','fas fa-circle'),(72,'mầm Brussels','fas fa-seedling'),(73,'Mù tạt Dijon','fas fa-flask'),(74,'quả óc chó','fas fa-tree');
/*!40000 ALTER TABLE `ingredients` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-13 15:45:12
