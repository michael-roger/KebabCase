# KebabCase

# KebabCase

0. Install the "brew" if you don't have it installed:

```/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"```

0. Install MySQL if you don't have it installed:

```brew install mysql@8.4```

```echo 'export PATH="/opt/homebrew/opt/mysql@8.4/bin:$PATH"' >> ~/.zshrc```

```brew restart mysql@8.4```

```brew services stop mysql@8.4```

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

GRANT
ALTER,
SELECT,
CREATE,
DELETE,
DROP,
INDEX,
INSERT
ON
kebabcase.*
TO `kebabuser` @`localhost`;

FLUSH PRIVILEGES;
```

2. Start the application and Hibernate will create the tables for you.

3. Run the following SQL statements to populate your database with data:

### users
```
INSERT INTO users (id, first_name, last_name, email_address, password, created_datetime, modified_datetime) VALUES
(1, 'John', 'Doe', 'john.doe@example.com', 'password123', '2024-01-01 10:00:00', '2024-01-01 10:00:00'),
(2, 'Jane', 'Smith', 'jane.smith@example.com', 'password456', '2024-02-01 10:00:00', '2024-02-01 10:00:00'),
(3, 'Emily', 'Johnson', 'emily.johnson@example.com', 'password789', '2024-03-01 10:00:00', '2024-03-01 10:00:00'),
(4, 'Luci', 'Feinberg', 'luci.feinberg@example.com', 'i<3elifia123', '2024-05-04 11:30:00', '2024-06-02 08:00:00');
```

### clients
```
INSERT INTO clients (id, name, created_datetime, modified_datetime) VALUES
(1, 'HousingForAll', '2024-01-05 09:30:00', '2024-01-05 09:30:00'),
(2, 'AccessibleHomes', '2024-02-10 11:00:00', '2024-02-10 11:00:00'),
(3, 'BronxHousing', '2024-02-05 11:00:00', '2024-02-10 11:00:00'),
(4, 'BrooklynHousing', '2024-03-12 11:00:00', '2024-02-10 11:00:00');
```

### tokens
```
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
```

### buildings
```
INSERT INTO buildings (id, address, city, state, zip_code, created_datetime, modified_datetime) VALUES
(1, '123 Elm St', 'Brooklyn', 'NY', '62701', '2024-01-20 14:30:00', '2024-01-20 14:30:00'),
(2, '456 Oak Ave', 'Brooklyn', 'NY', '46142', '2024-02-22 14:30:00', '2024-02-22 14:30:00'),
(3, '789 Maple Blvd', 'Bronx', 'NY', '43215', '2024-03-15 14:30:00', '2024-03-15 14:30:00'),
(4, '111 Jojo St', 'Bronx', 'NY', '99999', '2024-04-15 14:30:00', '2024-03-15 14:30:00');
```

### housing_units
```
INSERT INTO housing_units (id, building_id, unit_number, created_datetime, modified_datetime) VALUES
(1, 1, '1A', '2024-01-21 10:00:00', '2024-01-21 10:00:00'),
(2, 1, '1B', '2024-01-21 10:00:00', '2024-01-21 10:00:00'),
(3, 2, '2A', '2024-02-23 10:00:00', '2024-02-23 10:00:00'),
(4, 3, '3C', '2024-03-16 10:00:00', '2024-03-16 10:00:00'),
(5, 2, '8D', '2024-05-25 10:00:00', '2024-02-23 10:00:00'),
(6, 4, '4A', '2024-07-08 10:00:00', '2024-03-16 10:00:00');
```

### building_features
```
INSERT INTO building_features (id, name) VALUES
(1, 'Elevator'),
(2, 'Ramps'),
(3, 'Near Hospital');
```

### housing_unit_features
```
INSERT INTO housing_unit_features (id, name) VALUES
(1, 'Wheelchair Accessible'),
(2, 'Walk-in Shower'),
(3, 'Ground Floor');
```

### building_feature_building_mappings
```
INSERT INTO building_feature_building_mappings (id, building_id, building_feature_id) VALUES
(1, 1, 1),  -- Building 1 has Elevator
(2, 1, 2),  -- Building 1 has Ramps
(3, 2, 3),  -- Building 2 is Near Hospital
(4, 3, 1),  -- Building 3 has Elevator
(5, 4, 2),  -- Building 4 has Ramps
(6, 4, 1),  -- Building 4 has Elevator
(7, 3, 3);  -- Building 3 is Near Hospital
```

### housing_unit_feature_housing_unit_mappings
```
INSERT INTO housing_unit_feature_housing_unit_mappings (id, housing_unit_id, housing_unit_feature_id) VALUES
(1, 1, 1),  -- Housing unit 1A has Wheelchair Accessibility
(2, 2, 2),  -- Housing unit 1B has a Walk-in Shower
(3, 3, 3),  -- Housing unit 2A is on the Ground Floor
(4, 4, 1),  -- Housing unit 3C is on the Ground Floor
(5, 1, 2),  -- Housing unit 1A has a Walk-in Shower
(6, 2, 1),  -- Housing unit 1B has Wheelchair Accessibility
(7, 5, 3),  -- Housing unit 8D is on the Ground Floor
(8, 6, 1);  -- Housing unit 4A has Wheelchair Accessibility
```

## API Endpoints

### GET /housing-units

**Description**: Retrieves a list of available housing units with specific attributes.

**URL**: `/housing-units`

**Method**: `GET`

**URL Parameters**: None

**Success Response**:

- **Code**: 200 OK
- **Content**:

    ```json
    [
      {
        "id": 1,
        "unit_number": "1A",
        "building_id": 1,
        "attributes": {
          // Specific attributes
        }
      },
      // More housing units
    ]
    ```

**Sample Call**:

```bash
curl -X GET http://localhost:8080/housing-units
```

### GET /housing-unit/{id}

**Description**: Retrieves the specific details of a housing unit by its ID.

**URL**: `/housing-unit/{id}`

**Method**: `GET`

**URL Parameters**:
| Parameter   | Type   | Description               |
|-------------|--------|---------------------------|
| `id`        | `int`  | The ID of housing unit    |


**Success Response**:


- **Code**: 200 OK
- **Content**:

    ```json
    {
    "id": 1,
    "unit_number": "1A",
    "building_id": 1,
    "attributes": {
        // Specific attributes
    },
    "created_datetime": "2024-01-21T10:00:00",
    "modified_datetime": "2024-01-21T10:00:00"
    }
    ```

**Sample Call**:

```bash
curl -X GET http://localhost:8080/housing-unit/1
```

### GET /buildings

**Description**: Retrieves a list of buildings with specific attributes.

**URL**: `/buildings`

**Method**: `GET`

**URL Parameters**: None


**Success Response**:

- **Code**: 200 OK
- **Content**:

    ```json
    [
    {
        "id": 1,
        "address": "123 Elm St",
        "city": "Brooklyn",
        "state": "NY",
        "zip_code": "62701",
        "attributes": {
        // Specific building attributes
        }
    },
    // More buildings
    ]
    ```

**Sample Call**:

```bash
curl -X GET http://localhost:8080/buildings
```






### GET /building/{id}

**Description**: Retrieves information about a specific building by its ID.

**URL**: `/building/{id}`

**Method**: `GET`

**URL Parameters**: 
| Parameter   | Type   | Description               |
|-------------|--------|---------------------------|
| `id`        | `int`  | The ID of building   |


**Success Response**:

- **Code**: 200 OK
- **Content**:

    ```json
    {
    "id": 1,
    "address": "123 Elm St",
    "city": "Brooklyn",
    "state": "NY",
    "zip_code": "62701",
    "created_datetime": "2024-01-20T14:30:00",
    "modified_datetime": "2024-01-20T14:30:00"
    }
    ```

**Sample Call**:

```bash
curl -X GET http://localhost:8080/building/1
```


### GET /building/{id}/housing-units
**Description**: Retrieves all available housing units inside a building with a specific ID.

**URL**: `/building/{id}/housing-units`

**Method**: `GET`

**URL Parameters**: 
| Parameter   | Type   | Description               |
|-------------|--------|---------------------------|
| `id`        | `int`  | The ID of building   |


**Success Response**:

- **Code**: 200 OK
- **Content**:

    ```json
    [
    {
        "id": 1,
        "unit_number": "1A"
    },
    {
        "id": 2,
        "unit_number": "1B"
    }
    ]
    ```

**Sample Call**:

```bash
curl -X GET http://localhost:8080/building/1/housing-units
```



### POST /building
**Description**: Retrieves all available housing units inside a building with a specific ID.

**URL**: `/building`

**Method**: `POST`

**URL Parameters**: 
| Parameter   | Type   | Description               |
|-------------|--------|---------------------------|
| `address`        | `String`  | The building address   |
| `city`        | `String`  | The city the building is located in   |
| `state`        | `String`  | 	The state the building is located in   |
| `zip_code`        | `String`  | The postal code of the building  |


**Success Response**:

- **Code**: 201 Created
- **Content**:

    ```json
    {
    "id": 5,
    "address": "500 Pine St",
    "city": "Brooklyn",
    "state": "NY",
    "zip_code": "62703",
    "created_datetime": "2024-10-17T12:00:00",
    "modified_datetime": "2024-10-17T12:00:00"
    }
    ```

**Sample Call**:

```bash
curl -X POST http://localhost:8080/building -d '{"address": "500 Pine St", "city": "Brooklyn", "state": "NY", "zip_code": "62703"}' -H "Content-Type: application/json"
```


### PATCH  /housing-unit/{id}
**Description**: Allows for the update of a specific housing unit.

**URL**: ` /housing-unit/{id}`

**Method**: `PATCH`

**URL Parameters**: 
| Parameter   | Type   | Description               |
|-------------|--------|---------------------------|
| `id`        | `int`  | The ID of the housing unit  |

**Body Parameters**: 
| Parameter   | Type   | Description               |
|-------------|--------|---------------------------|
| `unit_number`        | `String`  | The new unit number of the housing unit   |


**Success Response**:

- **Code**: 200 OK
- **Content**:

    ```json
    {
    "id": 1,
    "unit_number": "1B Updated",
    "building_id": 1,
    "created_datetime": "2024-01-21T10:00:00",
    "modified_datetime": "2024-10-17T12:00:00"
    }
    ```

**Sample Call**:

```bash
curl -X PATCH http://localhost:8080/housing-unit/1 -d '{"unit_number": "1B Updated"}' -H "Content-Type: application/json"
```



### PATCH  /building/{id}
**Description**: Allows for the update of a specific building.

**URL**: `/building/{id}`

**Method**: `PATCH`

**URL Parameters**: 
| Parameter   | Type   | Description               |
|-------------|--------|---------------------------|
| `id`        | `int`  | The ID of the housing unit  |

**Body Parameters**: 
| Parameter   | Type   | Description               |
|-------------|--------|---------------------------|
| `address`        | `String`  | The new building address   |


**Success Response**:

- **Code**: 200 OK
- **Content**:

    ```json
    {
    "id": 1,
    "address": "500 Oak St",
    "city": "Brooklyn",
    "state": "NY",
    "zip_code": "62701",
    "created_datetime": "2024-01-20T14:30:00",
    "modified_datetime": "2024-10-17T12:00:00"
    }
    ```

**Sample Call**:

```bash
curl -X PATCH http://localhost:8080/building/1 -d '{"address": "500 Oak St"}' -H "Content-Type: application/json"
```

## Testing
This project uses **JUnit** for unit testing, **MockMVC**, **JaCoCo** for code coverage, Maven **Checkstyle** for enforcing code style, and **PMD** for static code analysis.

### Before you start testing, make sure you have the following:
- **Maven**
-  **Java 17**

## **JUnit Testing**
Use the following command to run all unit tests located at src/test/java/:
```
mvn test
```

## **JaCoCo Coverage**
Use the following command to generate a Jacoco Coverage report:
```
mvn jacoco:report
```
This will generate a report under the target/site/jacoco directory. Refer to/view the index.html to view the report.

##**Static Analysis**
To perform static analysis with PMD, run the following command:
```
mvn pmd:pmd
```
This generates a static analysis report under target/site/pmd.html.

##**Checkstyle**
To checkstyle, run the following command:
```
mvn checkstyle:check
```
We seek for no checkstyle violations or warnings.
