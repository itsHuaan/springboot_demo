-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Nov 04, 2024 at 05:52 PM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `dbtest`
--

-- --------------------------------------------------------

--
-- Table structure for table `tbl_attendance`
--

CREATE TABLE `tbl_attendance` (
  `attendance_id` bigint(20) NOT NULL,
  `check_in` time(6) DEFAULT NULL,
  `check_out` time(6) DEFAULT NULL,
  `date` date DEFAULT NULL,
  `student_id` bigint(20) NOT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `check_in_status` varchar(255) DEFAULT NULL,
  `check_out_status` varchar(255) DEFAULT NULL,
  `is_paid_leave` bit(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_vietnamese_ci;

--
-- Dumping data for table `tbl_attendance`
--

INSERT INTO `tbl_attendance` (`attendance_id`, `check_in`, `check_out`, `date`, `student_id`, `notes`, `check_in_status`, `check_out_status`, `is_paid_leave`) VALUES
(1, '23:42:22.000000', '23:50:28.000000', '2024-11-04', 1, NULL, 'absent', '', b'1'),
(2, '23:42:26.000000', '23:50:33.000000', '2024-11-04', 2, NULL, 'absent', '', b'1'),
(3, '23:42:28.000000', '23:50:37.000000', '2024-11-04', 3, NULL, 'absent', '', b'1'),
(4, '23:42:31.000000', '23:50:40.000000', '2024-11-04', 4, NULL, 'absent', '', b'1');

-- --------------------------------------------------------

--
-- Table structure for table `tbl_student`
--

CREATE TABLE `tbl_student` (
  `student_id` bigint(20) NOT NULL,
  `name` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_vietnamese_ci;

--
-- Dumping data for table `tbl_student`
--

INSERT INTO `tbl_student` (`student_id`, `name`) VALUES
(1, 'Nguyen Van A'),
(2, 'Tran Thi B'),
(3, 'Le Van C'),
(4, 'Pham Thi D'),
(5, 'Hoang Van E'),
(6, 'Ngo Thi F'),
(7, 'Vu Van G'),
(8, 'Dang Thi H'),
(9, 'Bui Van I'),
(10, 'Ngo Thi J');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `tbl_attendance`
--
ALTER TABLE `tbl_attendance`
  ADD PRIMARY KEY (`attendance_id`),
  ADD KEY `FKeeb551hswd2yhl6xd8u9t0ymu` (`student_id`);

--
-- Indexes for table `tbl_student`
--
ALTER TABLE `tbl_student`
  ADD PRIMARY KEY (`student_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `tbl_attendance`
--
ALTER TABLE `tbl_attendance`
  MODIFY `attendance_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `tbl_student`
--
ALTER TABLE `tbl_student`
  MODIFY `student_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `tbl_attendance`
--
ALTER TABLE `tbl_attendance`
  ADD CONSTRAINT `FKeeb551hswd2yhl6xd8u9t0ymu` FOREIGN KEY (`student_id`) REFERENCES `tbl_student` (`student_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
