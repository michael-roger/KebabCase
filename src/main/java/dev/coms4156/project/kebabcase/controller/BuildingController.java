package dev.coms4156.project.kebabcase.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.coms4156.project.kebabcase.entity.BuildingEntity;
import dev.coms4156.project.kebabcase.repository.BuildingRepositoryInterface;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

/**
 * BuildingController handles building-related operations.
 * It provides an endpoint to fetch building details by the building's ID.
 */
@RestController
public class BuildingController {

    private final BuildingRepositoryInterface buildingRepository;
    private final ObjectMapper objectMapper;

    /**
     * Constructor for BuildingController, used for injecting dependencies.
     *
     * @param buildingRepository The repository interface for accessing building data.
     * @param objectMapper The object mapper for transforming entities to JSON.
     */
    public BuildingController(BuildingRepositoryInterface buildingRepository, ObjectMapper objectMapper) {
        this.buildingRepository = buildingRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Retrieves a specific building by its ID and returns the details as a JSON object.
     *
     * @param id The ID of the building to retrieve.
     * @return An ObjectNode JSON object containing the building details.
     * @throws ResponseStatusException if the building with the given ID is not found, with an HTTP status of 404.
     */
    @GetMapping("/building/{id}")
    public ObjectNode getBuildingById(@PathVariable int id) {

        // Check if the building exists
        Optional<BuildingEntity> buildingRepositoryResult = this.buildingRepository.findById(id);
        if (buildingRepositoryResult.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Building with id " + id + " not found"
            );
        }

        BuildingEntity building = buildingRepositoryResult.get();

        // Transform building details into JSON format
        ObjectNode json = this.objectMapper.createObjectNode();
        json.put("id", building.getId());
        json.put("address", building.getAddress());
        json.put("city", building.getCity());
        json.put("state", building.getState());
        json.put("zip_code", building.getZipCode());
        json.put("created_datetime", building.getCreatedDatetime().toString());
        json.put("modified_datetime", building.getModifiedDatetime().toString());

        return json;
    }
}
