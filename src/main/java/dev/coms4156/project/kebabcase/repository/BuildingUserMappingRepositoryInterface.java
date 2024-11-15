package dev.coms4156.project.kebabcase.repository;

import dev.coms4156.project.kebabcase.entity.BuildingUserMappingEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("BuildingUserMappingRepository")
public interface BuildingUserMappingRepositoryInterface 
		extends JpaRepository<BuildingUserMappingEntity, Integer> {
        
	List<BuildingUserMappingEntity> findByUserId(int userId);
}
