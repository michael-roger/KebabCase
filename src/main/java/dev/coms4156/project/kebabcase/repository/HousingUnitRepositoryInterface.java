package dev.coms4156.project.kebabcase.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.coms4156.project.kebabcase.entity.BuildingEntity;
import dev.coms4156.project.kebabcase.entity.HousingUnitEntity;

@Repository("HousingUnitRepository")
public interface HousingUnitRepositoryInterface extends JpaRepository<HousingUnitEntity, Integer> {
  List<HousingUnitEntity> findByBuilding(BuildingEntity building);
  Optional<HousingUnitEntity> findByBuildingAndUnitNumber(BuildingEntity building, String unitNumber);
}
