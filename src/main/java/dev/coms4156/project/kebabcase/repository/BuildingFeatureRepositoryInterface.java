package dev.coms4156.project.kebabcase.repository;

import dev.coms4156.project.kebabcase.entity.BuildingFeatureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("BuildingFeatureRepository")
public interface BuildingFeatureRepositoryInterface extends JpaRepository<BuildingFeatureEntity, Integer> {
}
