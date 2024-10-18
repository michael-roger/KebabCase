package dev.coms4156.project.kebabcase.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.coms4156.project.kebabcase.entity.BuildingEntity;

@Repository("BuildingRepository")
public interface BuildingRepositoryInterface extends JpaRepository<BuildingEntity, Integer> {
  Optional<BuildingEntity> findByAddressAndCityAndStateAndZipCode(String address, String city, String state, String zipCode);
}
