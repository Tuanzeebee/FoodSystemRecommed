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
-- Table structure for table `recipe_ingredients`
--

DROP TABLE IF EXISTS `recipe_ingredients`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `recipe_ingredients` (
  `recipe_id` bigint NOT NULL,
  `ingredient_id` bigint NOT NULL,
  PRIMARY KEY (`recipe_id`,`ingredient_id`),
  KEY `ingredient_id` (`ingredient_id`),
  CONSTRAINT `recipe_ingredients_ibfk_1` FOREIGN KEY (`recipe_id`) REFERENCES `recipes` (`id`),
  CONSTRAINT `recipe_ingredients_ibfk_2` FOREIGN KEY (`ingredient_id`) REFERENCES `ingredients` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `recipe_ingredients`
--

LOCK TABLES `recipe_ingredients` WRITE;
/*!40000 ALTER TABLE `recipe_ingredients` DISABLE KEYS */;
INSERT INTO `recipe_ingredients` VALUES (1,1),(2,1),(7,1),(17,1),(1,2),(2,2),(5,2),(6,2),(7,2),(10,2),(11,2),(17,2),(20,2),(21,2),(1,3),(2,3),(7,3),(17,3),(1,4),(2,4),(7,4),(17,4),(1,5),(2,5),(7,5),(17,5),(1,6),(2,6),(3,6),(4,6),(6,6),(7,6),(8,6),(9,6),(11,6),(12,6),(15,6),(17,6),(18,6),(19,6),(21,6),(22,6),(25,6),(1,7),(2,7),(4,7),(6,7),(7,7),(9,7),(11,7),(13,7),(14,7),(15,7),(16,7),(17,7),(19,7),(21,7),(23,7),(24,7),(25,7),(26,7),(1,8),(2,8),(7,8),(14,8),(17,8),(24,8),(2,9),(7,9),(17,9),(2,10),(7,10),(17,10),(2,11),(7,11),(17,11),(2,12),(6,12),(7,12),(11,12),(17,12),(21,12),(2,13),(3,13),(5,13),(6,13),(7,13),(8,13),(10,13),(11,13),(14,13),(15,13),(17,13),(18,13),(20,13),(21,13),(24,13),(25,13),(3,14),(8,14),(18,14),(3,15),(8,15),(18,15),(3,16),(8,16),(12,16),(18,16),(22,16),(3,17),(8,17),(18,17),(4,18),(9,18),(19,18),(4,19),(9,19),(19,19),(5,20),(10,20),(20,20),(5,21),(6,21),(10,21),(11,21),(20,21),(21,21),(5,22),(10,22),(20,22),(5,23),(10,23),(12,23),(20,23),(22,23),(5,24),(10,24),(20,24),(5,25),(10,25),(20,25),(5,26),(10,26),(20,26),(5,27),(10,27),(14,27),(15,27),(20,27),(24,27),(25,27),(6,28),(11,28),(12,28),(21,28),(22,28),(6,29),(11,29),(21,29),(6,30),(11,30),(21,30),(6,31),(11,31),(21,31),(6,32),(11,32),(21,32),(6,33),(11,33),(21,33),(6,34),(11,34),(21,34),(6,35),(11,35),(14,35),(21,35),(24,35),(6,36),(11,36),(21,36),(6,37),(11,37),(21,37),(6,38),(11,38),(21,38),(6,39),(11,39),(21,39),(6,40),(11,40),(21,40),(6,41),(11,41),(21,41),(12,42),(22,42),(12,43),(22,43),(12,44),(22,44),(12,45),(22,45),(12,46),(22,46),(12,47),(22,47),(12,48),(22,48),(12,49),(22,49),(12,50),(15,50),(22,50),(25,50),(13,51),(23,51),(13,52),(23,52),(13,53),(23,53),(13,54),(23,54),(13,55),(23,55),(13,56),(16,56),(23,56),(26,56),(13,57),(23,57),(13,58),(23,58),(13,59),(16,59),(23,59),(26,59),(13,60),(23,60),(13,61),(23,61),(13,62),(23,62),(14,63),(24,63),(14,64),(24,64),(14,65),(24,65),(14,66),(24,66),(14,67),(24,67),(15,68),(25,68),(15,69),(25,69),(15,70),(25,70),(16,71),(26,71),(16,72),(26,72),(16,73),(26,73),(16,74),(26,74);
/*!40000 ALTER TABLE `recipe_ingredients` ENABLE KEYS */;
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
