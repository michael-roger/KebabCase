package dev.coms4156.project.kebabcase.repository;

import dev.coms4156.project.kebabcase.entity.HousingUnitUserMappingEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("HousingUnitUserMappingRepository")
public interface HousingUnitUserMappingRepositoryInterface 
		extends JpaRepository<HousingUnitUserMappingEntity, Integer> {

			List<HousingUnitUserMappingEntity> findByUserId(int userId);
	
}
