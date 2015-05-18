-- phpMyAdmin SQL Dump
-- version 3.5.5
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Apr 16, 2014 at 11:03 AM
-- Server version: 5.5.31
-- PHP Version: 5.4.11

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `subscription`
--
USE `subscription`;

-- --------------------------------------------------------

--
-- Table structure for table `subscription`
--

CREATE TABLE IF NOT EXISTS `subscription` (
  `handphone` varchar(20) NOT NULL,
  `email` varchar(50) NOT NULL,
  `region` varchar(10) NOT NULL,
  `handphone_verified` tinyint(1) NOT NULL DEFAULT '1',
  `email_verified` tinyint(1) NOT NULL DEFAULT '1',
  `longtitude` float NOT NULL,
  `latitude` float NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `email_hash` varchar(100) NOT NULL,
  `handphone_hash` varchar(10) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='table for storage of subscription data' AUTO_INCREMENT=7 ;

--
-- Dumping data for table `subscription`
--

INSERT INTO `subscription` (`handphone`, `email`, `region`, `handphone_verified`, `email_verified`, `longtitude`, `latitude`, `create_time`, `id`, `email_hash`, `handphone_hash`) VALUES
('+6594681497', 'lisi0010@e.ntu.edu.sg', '0', 1, 1, 103.712, 1.32785, '2014-04-16 02:07:25', 6, '288558f190939f59eca321ce29c776051f1ae01eac221139d2f41fc3', '8b4af1');

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
