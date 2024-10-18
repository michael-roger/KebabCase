package dev.coms4156.project.kebabcase.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.coms4156.project.kebabcase.entity.BuildingEntity;
import dev.coms4156.project.kebabcase.entity.BuildingFeatureBuildingMappingEntity;
import dev.coms4156.project.kebabcase.repository.BuildingRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.BuildingFeatureBuildingMappingRepositoryInterface;
import dev.coms4156.project.kebabcase.repository.HousingUnitRepositoryInterface;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/** TODO */
@RestController
public class BuildingController {
  private final BuildingRepositoryInterface buildingRepository;

  private final BuildingFeatureBuildingMappingRepositoryInterface buildingFeatureMappingRespository;

  private final ObjectMapper objectMapper;

  public BuildingController(
    BuildingRepositoryInterface buildingRepository,
    BuildingFeatureBuildingMappingRepositoryInterface buildingFeatureMappingRepository,
    ObjectMapper objectMapper
  ) {
    this.buildingRepository = buildingRepository;
    this.buildingFeatureMappingRespository = buildingFeatureMappingRepository;
    this.objectMapper = objectMapper;
  }


  /*
   * Displays a list of all buildings with the desired building feature
   */

  @GetMapping("/buildings/{id}/features")
  public List<ObjectNode> getBuildingHousingUnits(@PathVariable int id) {

    List<BuildingFeatureBuildingMappingEntity> result =
      this.buildingFeatureMappingRespository.findByBuildingFeatureId(id);

      if (result.isEmpty()) {
        throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, "Building with feature id " + id + " not found"
        );
      }

    return result.stream().map(mapping -> {
      BuildingEntity building = mapping.getBuilding();
      ObjectNode Json = objectMapper.createObjectNode();
      Json.put("id", building.getId());
      Json.put("building_address", building.getAddress());
      Json.put("city", building.getCity());
      Json.put("state", building.getState());
      Json.put("zipcode", building.getZipCode());
      return Json;
    }).collect(Collectors.toList());
  }
}
