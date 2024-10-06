# KebabCase


CREATE USER `kebabuser`@`localhost` IDENTIFIED BY 'kebabpass';

GRANT Alter, Alter Routine, Create, Create Routine, Create Temporary Tables, Create User, Create View, Delete, Drop, Event, Execute, File, Index, Insert, Lock Tables, Process, References, Reload, Replication Client, Replication Slave, Select, Show Databases, Show View, Shutdown, Trigger, Update ON *.* TO `kebabuser`@`localhost`;
