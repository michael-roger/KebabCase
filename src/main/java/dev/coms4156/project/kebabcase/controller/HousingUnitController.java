package dev.coms4156.project.kebabcase.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.coms4156.project.kebabcase.entity.BuildingEntity;
import dev.coms4156.project.kebabcase.entity.HousingUnitEntity;
import dev.coms4156.project.kebabcase.repository.BuildingRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.HousingUnitRepositoryInterface;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/** TODO */
@RestController
public class HousingUnitController {
  private final HousingUnitRepositoryInterface housingUnitRepository;

  private final BuildingRepositoryInterface buildingRepository;

  private final ObjectMapper objectMapper;

  public HousingUnitController(
      HousingUnitRepositoryInterface housingUnitRepository,
      BuildingRepositoryInterface buildingRepository,
      ObjectMapper objectMapper
  ) {
    this.housingUnitRepository = housingUnitRepository;
    this.buildingRepository = buildingRepository;
    this.objectMapper = objectMapper;
  }

  /*
  *  Displays a list of all available housing units (apartments)
  */

//

  @GetMapping("/housing units")
  public List<ObjectNode> getAllHousingUnits() {

    List<HousingUnitEntity> housingResult = this.housingUnitRepository.findAll();

    if (housingResult.isEmpty()) {
      throw new ResponseStatusException(
              HttpStatus.NOT_FOUND, "No available housing units founds"
      );
    }

    return housingResult.stream().map(housingUnit -> {
      ObjectNode Json = this.objectMapper.createObjectNode();
      Json.put("building_id", housingUnit.getBuildingId());
      Json.put("id", housingUnit.getId());
      Json.put("created_datetime", housingUnit.getCreatedDatetime().toEpochSecond());
      Json.put("modified_datetime", housingUnit.getModifiedDatetime().toEpochSecond());
      Json.put("unit_number", housingUnit.getUnitNumber());
      return Json;
    }).collect(Collectors.toList());
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
}
