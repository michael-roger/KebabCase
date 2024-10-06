package dev.coms4156.project.kebabcase.repository;

import dev.coms4156.project.kebabcase.entity.ClientEntity;
import dev.coms4156.project.kebabcase.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("ClientRepository")
public interface ClientRepositoryInterface extends JpaRepository<ClientEntity, Integer> {
}
