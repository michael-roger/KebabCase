# KebabCase

## Insert Dummy Data SQL Statements

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
INSERT INTO tokens (id, client_id, user_id, key, expiration_datetime, created_datetime, modified_datetime) VALUES
(1, 1, 1, 'john_doe_token', '2024-04-01 12:00:00', '2024-01-01 10:15:00', '2024-01-01 10:15:00'),
(2, 2, 2, 'jane_smith_token', '2024-05-01 12:00:00', '2024-02-01 10:15:00', '2024-02-01 10:15:00'),
(3, 1, 3, 'emily_johnson_token', '2024-06-01 12:00:00', '2024-03-01 10:15:00', '2024-03-01 10:15:00'),
(4, 2, 4, 'luci_feinberg_token', '2024-06-05 12:00:00', '2024-03-01 10:15:00', '2024-03-01 10:15:00');

INSERT INTO tokens (id, client_id, key, expiration_datetime, created_datetime, modified_datetime) VALUES
(5, 1, 'HousingForAll_token', '2024-04-01 12:00:00', '2024-01-01 10:15:00', '2024-01-01 10:15:00'),
(6, 2, 'AccessibleHomes_token', '2024-05-01 12:00:00', '2024-02-01 10:15:00', '2024-02-01 10:15:00'),
(7, 3, 'BronxHousing_token', '2024-06-01 12:00:00', '2024-03-01 10:15:00', '2024-03-01 10:15:00'),
(8, 4, 'BrooklynHousing_token', '2024-06-05 12:00:00', '2024-03-01 10:15:00', '2024-03-01 10:15:00'),;
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
(6, 4, '4A', '2024-07-08 10:00:00', '2024-03-16 10:00:00'),;
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
