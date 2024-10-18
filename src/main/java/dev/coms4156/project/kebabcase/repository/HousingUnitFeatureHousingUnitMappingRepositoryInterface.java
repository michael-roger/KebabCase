package dev.coms4156.project.kebabcase.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.coms4156.project.kebabcase.entity.HousingUnitEntity;
import dev.coms4156.project.kebabcase.entity.HousingUnitFeatureEntity;
import dev.coms4156.project.kebabcase.entity.HousingUnitFeatureHousingUnitMappingEntity;

@Repository("HousingUnitFeatureHousingUnitMappingRepository")
public interface HousingUnitFeatureHousingUnitMappingRepositoryInterface extends JpaRepository<HousingUnitFeatureHousingUnitMappingEntity, Integer> {
        Optional<HousingUnitFeatureHousingUnitMappingEntity> findByHousingUnitAndHousingUnitFeature(HousingUnitEntity housingUnit, HousingUnitFeatureEntity housingUnitFeature);
}
