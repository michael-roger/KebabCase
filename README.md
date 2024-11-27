# KebabCase

## Jira
https://kebab-case.atlassian.net/jira/software/projects/KAN/boards/1

## API Endpoints
https://app.swaggerhub.com/apis/TO2428/KebabCase/1.0.0

## Testing
This project uses **JUnit** for unit testing, **JaCoCo** for code coverage, Maven **Checkstyle** for enforcing code style, and **PMD** for static code analysis.

### Before you start testing, make sure you have the following:
- **Maven**
-  **Java 17**

## **JUnit Testing**
Use the following command to run all unit tests located at src/test/java/:
```
./mvnw test
```

## **JaCoCo Coverage**
Use the following command to generate a Jacoco Coverage report:
```
./mvnw jacoco:report
```
This will generate a report under the target/site/jacoco directory. Refer to view the index.html to view the report.

## **Static Analysis**
To perform static analysis with PMD, run the following command:
```
./mvnw pmd:pmd
```
This generates a static analysis report under target/site/pmd.html.

## **Checkstyle**
To checkstyle, run the following command:
```
./mvnw checkstyle:check
```
We seek for no checkstyle violations or warnings.  The following is our report as of October 18, 2024.

![Checkstyle](/reports/checkstyle.png)

## First time startup instructions
0. Install the "brew" if you don't have it installed:

```/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"```

0. Install MySQL if you don't have it installed:

```brew install mysql@8.4```

```echo 'export PATH="/opt/homebrew/opt/mysql@8.4/bin:$PATH"' >> ~/.zshrc```

```brew services restart mysql@8.4```

```brew services stop mysql@8.4```

``exit``

Open new terminal

```mysql_secure_installation```

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
    SET = 'utf8mb4' DEFAULT COLLATE 'utf8mb4_unicode_520_ci';

CREATE USER `kebabuser`@`localhost` IDENTIFIED BY 'kebabpass';

GRANT
ALTER,
SELECT,
CREATE,
DELETE,
DROP,
INDEX,
INSERT,
REFERENCES,
UPDATE
ON
kebabcase.*
TO `kebabuser` @`localhost`;

FLUSH PRIVILEGES;
```

2. Set the following line to "create" in application.properties:

spring.jpa.hibernate.ddl-auto=create

Start the application and Hibernate will create the tables for you.

Be sure to change the line back to "none" after the first time you start the application:

spring.jpa.hibernate.ddl-auto=none

3. Run the following SQL statements to populate your database with data:

```
-- users
INSERT INTO users (id, first_name, last_name, email_address, password, created_datetime, modified_datetime) VALUES
(1, 'John', 'Doe', 'john.doe@example.com', 'password123', '2024-01-01 10:00:00', '2024-01-01 10:00:00'),
(2, 'Jane', 'Smith', 'jane.smith@example.com', 'password456', '2024-02-01 10:00:00', '2024-02-01 10:00:00'),
(3, 'Emily', 'Johnson', 'emily.johnson@example.com', 'password789', '2024-03-01 10:00:00', '2024-03-01 10:00:00'),
(4, 'Luci', 'Feinberg', 'luci.feinberg@example.com', 'i<3elifia123', '2024-05-04 11:30:00', '2024-06-02 08:00:00');


-- clients
INSERT INTO clients (id, name, created_datetime, modified_datetime) VALUES
(1, 'HousingForAll', '2024-01-05 09:30:00', '2024-01-05 09:30:00'),
(2, 'AccessibleHomes', '2024-02-10 11:00:00', '2024-02-10 11:00:00'),
(3, 'BronxHousing', '2024-02-05 11:00:00', '2024-02-10 11:00:00'),
(4, 'BrooklynHousing', '2024-03-12 11:00:00', '2024-02-10 11:00:00'),
(5, 'HomeSweetHome', '2024-03-12 11:00:00', '2024-02-10 11:00:00');

-- tokens
INSERT INTO tokens (id, client_id, user_id, token, expiration_datetime, created_datetime, modified_datetime) VALUES
(1, 1, 1, 'john_doe_token', '2024-04-01 12:00:00', '2024-01-01 10:15:00', '2024-01-01 10:15:00'),
(2, 2, 2, 'jane_smith_token', '2024-05-01 12:00:00', '2024-02-01 10:15:00', '2024-02-01 10:15:00'),
(3, 1, 3, 'emily_johnson_token', '2024-06-01 12:00:00', '2024-03-01 10:15:00', '2024-03-01 10:15:00'),
(4, 2, 4, 'luci_feinberg_token', '2024-06-05 12:00:00', '2024-03-01 10:15:00', '2024-03-01 10:15:00');

INSERT INTO tokens (id, client_id, token, expiration_datetime, created_datetime, modified_datetime) VALUES
(5, 1, 'HousingForAll_token', '2024-04-01 12:00:00', '2024-01-01 10:15:00', '2024-01-01 10:15:00'),
(6, 2, 'AccessibleHomes_token', '2024-05-01 12:00:00', '2024-02-01 10:15:00', '2024-02-01 10:15:00'),
(7, 3, 'BronxHousing_token', '2024-06-01 12:00:00', '2024-03-01 10:15:00', '2024-03-01 10:15:00'),
(8, 4, 'BrooklynHousing_token', '2024-06-05 12:00:00', '2024-03-01 10:15:00', '2024-03-01 10:15:00');

-- buildings
INSERT INTO buildings (id, address, city, state, zip_code, created_datetime, modified_datetime) VALUES
(1, '123 Elm St', 'Brooklyn', 'NY', '62701', '2024-01-20 14:30:00', '2024-01-20 14:30:00'),
(2, '456 Oak Ave', 'Brooklyn', 'NY', '46142', '2024-02-22 14:30:00', '2024-02-22 14:30:00'),
(3, '789 Maple Blvd', 'Bronx', 'NY', '43215', '2024-03-15 14:30:00', '2024-03-15 14:30:00'),
(4, '111 Jojo St', 'Bronx', 'NY', '99999', '2024-04-15 14:30:00', '2024-03-15 14:30:00');


-- housing_units
INSERT INTO housing_units (id, building_id, unit_number, created_datetime, modified_datetime) VALUES
(1, 1, '1A', '2024-01-21 10:00:00', '2024-01-21 10:00:00'),
(2, 1, '1B', '2024-01-21 10:00:00', '2024-01-21 10:00:00'),
(3, 2, '2A', '2024-02-23 10:00:00', '2024-02-23 10:00:00'),
(4, 3, '3C', '2024-03-16 10:00:00', '2024-03-16 10:00:00'),
(5, 2, '8D', '2024-05-25 10:00:00', '2024-02-23 10:00:00'),
(6, 4, '4A', '2024-07-08 10:00:00', '2024-03-16 10:00:00');


-- building_features
INSERT INTO building_features (id, name) VALUES
(1, 'Elevator'),
(2, 'Ramps'),
(3, 'Near Hospital');


-- housing_unit_features
INSERT INTO housing_unit_features (id, name) VALUES
(1, 'Wheelchair Accessible'),
(2, 'Walk-in Shower'),
(3, 'Ground Floor');


-- building_feature_building_mappings
INSERT INTO building_feature_building_mappings (id, building_id, building_feature_id) VALUES
(1, 1, 1),  -- Building 1 has Elevator
(2, 1, 2),  -- Building 1 has Ramps
(3, 2, 3),  -- Building 2 is Near Hospital
(4, 3, 1),  -- Building 3 has Elevator
(5, 4, 2),  -- Building 4 has Ramps
(6, 4, 1),  -- Building 4 has Elevator
(7, 3, 3);  -- Building 3 is Near Hospital


-- housing_unit_feature_housing_unit_mappings
INSERT INTO housing_unit_feature_housing_unit_mappings (id, housing_unit_id, housing_unit_feature_id) VALUES
(1, 1, 1),  -- Housing unit 1A has Wheelchair Accessibility
(2, 2, 2),  -- Housing unit 1B has a Walk-in Shower
(3, 3, 3),  -- Housing unit 2A is on the Ground Floor
(4, 4, 1),  -- Housing unit 3C is on the Ground Floor
(5, 1, 2),  -- Housing unit 1A has a Walk-in Shower
(6, 2, 1),  -- Housing unit 1B has Wheelchair Accessibility
(7, 5, 3),  -- Housing unit 8D is on the Ground Floor
(8, 6, 1);  -- Housing unit 4A has Wheelchair Accessibility


-- housing_unit_user_mappings
INSERT INTO housing_unit_user_mappings (id, housing_unit_id, user_id, created_datetime, modified_datetime) VALUES
(1, 1, 1, '2024-01-21 10:05:00', '2024-01-21 10:05:00'),  -- John associated with Housing Unit 1A
(2, 2, 2, '2024-01-21 10:10:00', '2024-01-21 10:10:00'),  -- Jane associated with Housing Unit 1B
(3, 3, 3, '2024-02-23 10:05:00', '2024-02-23 10:05:00'),  -- Emily associated with Housing Unit 2A
(4, 4, 4, '2024-03-16 10:05:00', '2024-03-16 10:05:00'),  -- Luci associated with Housing Unit 3C
(5, 5, 1, '2024-02-25 10:00:00', '2024-02-25 10:00:00'),  -- John associated with Housing Unit 8D
(6, 6, 3, '2024-07-08 10:05:00', '2024-07-08 10:05:00');  -- Emily associated with Housing Unit 4A


-- building_user_mappings 
INSERT INTO building_user_mappings (id, building_id, user_id, created_datetime, modified_datetime) VALUES
(1, 1, 1, '2024-01-20 14:35:00', '2024-01-20 14:35:00'),  -- John associated with Building 123 Elm St
(2, 2, 2, '2024-02-22 14:35:00', '2024-02-22 14:35:00'),  -- Jane associated with Building 456 Oak Ave
(3, 3, 3, '2024-03-15 14:35:00', '2024-03-15 14:35:00'),  -- Emily associated with Building 789 Maple Blvd
(4, 4, 4, '2024-04-15 14:35:00', '2024-04-15 14:35:00'),  -- Luci associated with Building 111 Jojo St
(5, 2, 3, '2024-02-25 14:40:00', '2024-02-25 14:40:00'),  -- Emily also associated with Building 456 Oak Ave
(6, 3, 2, '2024-03-18 14:45:00', '2024-03-18 14:45:00');  -- Jane also associated with Building 789 Maple Blvd


-- Permission table
INSERT INTO permissions (id, name, created_datetime, modified_datetime) VALUES
(1, 'view_housing_units', '2024-01-01 10:00:00', '2024-01-01 10:00:00'),  -- GET access for housing units
(2, 'create_housing_units', '2024-01-01 10:00:00', '2024-01-01 10:00:00'),  -- POST access for housing units
(3, 'edit_housing_units', '2024-01-01 10:00:00', '2024-01-01 10:00:00'),  -- PATCH access for housing units
(4, 'view_buildings', '2024-01-01 10:00:00', '2024-01-01 10:00:00'),  -- GET access for buildings
(5, 'create_buildings', '2024-01-01 10:00:00', '2024-01-01 10:00:00'),  -- POST access for buildings
(6, 'edit_buildings', '2024-01-01 10:00:00', '2024-01-01 10:00:00'),  -- PATCH access for buildings
(7, 'view_only', '2024-01-01 10:00:00', '2024-01-01 10:00:00');  -- View-only access


-- permission_client_mappings 
INSERT INTO permission_client_mappings (id, permission_id, client_id, created_datetime, modified_datetime) VALUES

-- HousingForAll Permissions
(1, 1, 1, '2024-01-01 10:00:00', '2024-01-01 10:00:00'),  -- view_housing_units
(2, 2, 1, '2024-01-01 10:00:00', '2024-01-01 10:00:00'),  -- create_housing_units
(3, 3, 1, '2024-01-01 10:00:00', '2024-01-01 10:00:00'),  -- edit_housing_units
(4, 4, 1, '2024-01-01 10:00:00', '2024-01-01 10:00:00'),  -- view_buildings
(5, 5, 1, '2024-01-01 10:00:00', '2024-01-01 10:00:00'),  -- create_buildings
(6, 6, 1, '2024-01-01 10:00:00', '2024-01-01 10:00:00'),  -- edit_buildings

-- AccessibleHomes Permissions
(7, 1, 2, '2024-01-01 10:00:00', '2024-01-01 10:00:00'),  -- view_housing_units
(8, 4, 2, '2024-01-01 10:00:00', '2024-01-01 10:00:00'),  -- view_buildings
(9, 5, 2, '2024-01-01 10:00:00', '2024-01-01 10:00:00'),  -- create_buildings
(10, 6, 2, '2024-01-01 10:00:00', '2024-01-01 10:00:00'),  -- edit_buildings

-- BronxHousing Permissions (view-only for housing units)
(11, 1, 3, '2024-01-01 10:00:00', '2024-01-01 10:00:00'),  -- view_housing_units

-- HomeSweetHome Permissions (view-only for housing units)
(13, 1, 5, '2024-01-01 10:00:00', '2024-01-01 10:00:00'),  -- view_housing_units

-- BrooklynHousing Permissions (view-only for housing units and buildings)
(14, 1, 4, '2024-01-01 10:00:00', '2024-01-01 10:00:00'),  -- view_housing_units
(15, 4, 4, '2024-01-01 10:00:00', '2024-01-01 10:00:00');  -- view_buildings
```
