package dev.coms4156.project.kebabcase.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.coms4156.project.kebabcase.entity.BuildingEntity;
import dev.coms4156.project.kebabcase.entity.BuildingFeatureBuildingMappingEntity;
import dev.coms4156.project.kebabcase.entity.BuildingFeatureEntity;

@Repository("BuildingFeatureBuildingMappingRepository")
public interface BuildingFeatureBuildingMappingRepositoryInterface extends JpaRepository<BuildingFeatureBuildingMappingEntity, Integer> {
        Optional<BuildingFeatureBuildingMappingEntity> findByBuildingAndBuildingFeature(BuildingEntity building, BuildingFeatureEntity buildingFeature);

}
