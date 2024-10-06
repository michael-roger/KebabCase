# KebabCase

1. Run the following SQL commands to create the database:

CREATE DATABASE IF NOT EXISTS kebabcase
DEFAULT CHARACTER SET = 'utf8mb4' DEFAULT COLLATE 'utf8mb4_unicode_520_ci'

CREATE USER `kebabuser`@`localhost` IDENTIFIED BY 'kebabpass';

GRANT Alter, Alter Routine, Create, Create Routine, Create Temporary Tables, Create User, Create View, Delete, Drop, Event, Execute, File, Index, Insert, Lock Tables, Process, References, Reload, Replication Client, Replication Slave, Select, Show Databases, Show View, Shutdown, Trigger, Update ON *.* TO `kebabuser`@`localhost`;


2. Start the application and Hibernate will create the tables for you.

3. Run the following SQL statements to populate your database with data:

INSERT INTO users
SET created_datetime = NOW(),
modified_datetime = NOW(),
email_address = "michael.roger@columbia.edu",
first_name = "Michael",
last_name = "Roger",
`password` = "1234"
