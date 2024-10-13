package dev.coms4156.project.kebabcase.repository;

import dev.coms4156.project.kebabcase.entity.BuildingEntity;
import dev.coms4156.project.kebabcase.entity.HousingUnitEntity;
import dev.coms4156.project.kebabcase.entity.UserEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("HousingUnitRepository")
public interface HousingUnitRepositoryInterface extends JpaRepository<HousingUnitEntity, Integer> {
  List<HousingUnitEntity> findByBuilding(BuildingEntity building);
}
