package dev.coms4156.project.kebabcase.controller;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.coms4156.project.kebabcase.entity.BuildingEntity;
import dev.coms4156.project.kebabcase.repository.BuildingRepositoryInterface;

/** TODO */
@RestController
public class BuildingController {
  
  private final BuildingRepositoryInterface buildingRepository;

  private final ObjectMapper objectMapper;


  public BuildingController(
      BuildingRepositoryInterface buildingRepository,
      ObjectMapper objectMapper
  ) {
    this.buildingRepository = buildingRepository;
    this.objectMapper = objectMapper;
  }

  @PatchMapping("/building/{id}")
  public ResponseEntity<?> updateBuilding(
      @PathVariable int id, 
      @RequestParam(required = false) String address,
      @RequestParam(required = false) String city,
      @RequestParam(required = false) String state,
      @RequestParam(required = false) String zipCode
  ) {

    Optional<BuildingEntity> buildingResult = buildingRepository.findById(id);

    if (buildingResult.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Building not found");
    }

    BuildingEntity building = buildingResult.get();

    if (address == null && 
        city == null && 
        state == null && 
        zipCode == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No fields provided for update");
    }

    if (address != null) {
        building.setAddress(address);
    }
    if (city != null) {
        building.setCity(city);
    }
    if (state != null) {
        building.setState(state);
    }
    if (zipCode != null) {
        building.setZipCode(zipCode);
    }

    building.setModifiedDatetime(OffsetDateTime.now());

    buildingRepository.save(building);

    return ResponseEntity.ok("Building info has been successfully updated!");

  }

  @PostMapping("/building")
  public ResponseEntity<?> createBuilding(
      @RequestParam String address,
      @RequestParam String city,
      @RequestParam String state,
      @RequestParam String zipCode
  ) {

    BuildingEntity newBuilding = new BuildingEntity();

    newBuilding.setAddress(address);
    newBuilding.setCity(city);
    newBuilding.setState(state);
    newBuilding.setZipCode(zipCode);
    newBuilding.setCreatedDatetime(OffsetDateTime.now());
    newBuilding.setModifiedDatetime(OffsetDateTime.now());

    BuildingEntity savedBuilding = buildingRepository.save(newBuilding);

    String response = "Building was added succesfully! Building ID: " + savedBuilding.getId().toString();

    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }


}