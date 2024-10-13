package dev.coms4156.project.kebabcase.repository;

import dev.coms4156.project.kebabcase.entity.HousingUnitFeatureEntity;
import dev.coms4156.project.kebabcase.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("HousingUnitFeatureRepository")
public interface HousingUnitFeatureRepositoryInterface extends JpaRepository<HousingUnitFeatureEntity, Integer> {
}
