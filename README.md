# KebabCase

0. Install the "brew" if you don't have it installed:

```/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"```

0. Install MySQL if you don't have it installed:

```brew install mysql@8.4```

```brew restart mysql@8.4```

```brew services stop mysql```

```mysqld_safe --skip-grant-tables &```

```mysql -u root```

```
USE mysql;
UPDATE user SET authentication_string=PASSWORD('YourPasswordGoesHere') WHERE User='root';
FLUSH PRIVILEGES;
exit;
```
Note: Be sure to update YourPasswordGoesHere with your own root password

```
mysqladmin -u root -p shutdown
```

```
brew services start mysql@8.4
```


1. Run the following SQL commands to create the database:

```
CREATE DATABASE
IF
    NOT EXISTS kebabcase DEFAULT CHARACTER
    SET = 'utf8mb4' DEFAULT COLLATE 'utf8mb4_unicode_520_ci'

CREATE USER `kebabuser`@`localhost` IDENTIFIED BY 'kebabpass';

GRANT ALTER,
ALTER Routine,
CREATE,
CREATE Routine,
CREATE TEMPORARY TABLES,
CREATE USER,
CREATE VIEW,
DELETE,
DROP,
EVENT,
EXECUTE,
File,
INDEX,
INSERT,
LOCK TABLES,
Process,
REFERENCES,
Reload,
Replication Client,
Replication SLAVE,
SELECT
    ,
    SHOW DATABASES,
    SHOW VIEW,
    Shutdown,
    TRIGGER,
    UPDATE ON *.* TO `kebabuser` @`localhost`;
```

2. Start the application and Hibernate will create the tables for you.

3. Run the following SQL statements to populate your database with data:

```
INSERT INTO users
SET created_datetime = NOW(),
modified_datetime = NOW(),
email_address = "michael.roger@columbia.edu",
first_name = "Michael",
last_name = "Roger",
`password` = "1234";
```
