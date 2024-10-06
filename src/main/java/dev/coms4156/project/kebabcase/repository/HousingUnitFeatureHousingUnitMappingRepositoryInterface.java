package dev.coms4156.project.kebabcase.repository;

import dev.coms4156.project.kebabcase.entity.HousingUnitFeatureHousingUnitMappingEntity;
import dev.coms4156.project.kebabcase.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("HousingUnitFeatureHousingUnitMappingRepository")
public interface HousingUnitFeatureHousingUnitMappingRepositoryInterface extends JpaRepository<HousingUnitFeatureHousingUnitMappingEntity, Integer> {
}
