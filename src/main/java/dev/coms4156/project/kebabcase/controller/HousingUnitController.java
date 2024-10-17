package dev.coms4156.project.kebabcase.controller;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import dev.coms4156.project.kebabcase.entity.BuildingEntity;
import dev.coms4156.project.kebabcase.entity.HousingUnitEntity;
import dev.coms4156.project.kebabcase.repository.BuildingRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.HousingUnitRepositoryInterface;

/** TODO */
@RestController
public class HousingUnitController {
  private final HousingUnitRepositoryInterface housingUnitRepository;

  private final BuildingRepositoryInterface buildingRepository;

  private final ObjectMapper objectMapper;

  private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;


  public HousingUnitController(
      HousingUnitRepositoryInterface housingUnitRepository,
      BuildingRepositoryInterface buildingRepository,
      ObjectMapper objectMapper
  ) {
    this.housingUnitRepository = housingUnitRepository;
    this.buildingRepository = buildingRepository;
    this.objectMapper = objectMapper;
  }

  @GetMapping("/building/{id}/housing-units")
  public List<ObjectNode> getBuildingHousingUnits(@PathVariable int id) {

    Optional<BuildingEntity> buildingRepositoryResult = this.buildingRepository.findById(id);

    if (buildingRepositoryResult.isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, "Building with id " + id + " not found"
      );
    }

    BuildingEntity building = buildingRepositoryResult.get();

    List<HousingUnitEntity>  housingUnits =
        this.housingUnitRepository.findByBuilding(building);

    return housingUnits.stream().map(housingUnit -> {
      ObjectNode Json = this.objectMapper.createObjectNode();
      Json.put("id", housingUnit.getId());
      Json.put("unit_number", housingUnit.getUnitNumber());
      return Json;
    }).collect(Collectors.toList());
  }

  /**
   * Retrieves a housing unit by its ID and returns its details as a JSON object.
   * <p>
   * This method uses the housing unit ID to search the repository for a corresponding
   * {@link HousingUnitEntity}. If found, the housing unit's details such as ID, building ID,
   * unit number, created date, and modified date are returned in JSON format. If the housing unit 
   * is not found, an HTTP 404 Not Found status is returned.
   * </p>
   *
   * @param id the ID of the housing unit to retrieve
   * @return an {@link ObjectNode} containing the housing unit's details
   * @throws ResponseStatusException if the housing unit with the given ID is not found
   */
  @GetMapping("/housing-unit/{id}")
  public ObjectNode getHousingUnit(@PathVariable int id) {
    
    Optional<HousingUnitEntity> housingUnitRepoResult = this.housingUnitRepository.findById(id);

    if (housingUnitRepoResult.isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, "Housing unit with id " + id + " not found"
      );
    }

    HousingUnitEntity unit = housingUnitRepoResult.get();

    ObjectNode json = this.objectMapper.createObjectNode();
    json.put("id", unit.getId());
    json.put("building_id", unit.getBuilding().getId());
    json.put("unit_number", unit.getUnitNumber());
    json.put("created_datetime", unit.getCreatedDatetime().format(formatter));
    json.put("modified_datetime", unit.getModifiedDatetime().format(formatter));

    return json;
  }


  @PatchMapping("/housing-unit/{id}")
  public ResponseEntity<?> updateBuilding(
      @PathVariable int id, 
      @RequestParam String unitNumber
  ) {

    Optional<HousingUnitEntity> unitResult = housingUnitRepository.findById(id);

    if (unitResult.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Building not found");
    }

    HousingUnitEntity unit = unitResult.get();

    unit.setUnitNumber(unitNumber);

    unit.setModifiedDatetime(OffsetDateTime.now());

    housingUnitRepository.save(unit);

    return ResponseEntity.ok("Housing unit info has been successfully updated!");

  }

  
  @PostMapping("/housing-unit")
  public ResponseEntity<?> createBuilding(
      @RequestParam int buildingID,
      @RequestParam String unitNumber
  ) {

    HousingUnitEntity newUnit = new HousingUnitEntity();

    Optional<BuildingEntity> buildingRepoResult = this.buildingRepository.findById(buildingID);

    if (buildingRepoResult.isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, "Building with id " + buildingID + " not found"
      );
    }

    BuildingEntity building = buildingRepoResult.get();

    newUnit.setBuilding(building);
    newUnit.setUnitNumber(unitNumber);
    newUnit.setCreatedDatetime(OffsetDateTime.now());
    newUnit.setModifiedDatetime(OffsetDateTime.now());

    HousingUnitEntity savedUnit = housingUnitRepository.save(newUnit);

    String response = "Housing Unit was added succesfully! Housing Unit ID: " + savedUnit.getId().toString();

    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

}
