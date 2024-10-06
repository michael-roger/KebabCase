package dev.coms4156.project.kebabcase.repository;

import dev.coms4156.project.kebabcase.entity.BuildingFeatureBuildingMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("BuildingFeatureBuildingMappingRepository")
public interface BuildingFeatureBuildingMappingRepositoryInterface extends JpaRepository<BuildingFeatureBuildingMappingEntity, Integer> {
}
