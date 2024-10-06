package dev.coms4156.project.kebabcase.repository;

import dev.coms4156.project.kebabcase.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("UserRepository")
public interface UserRepositoryInterface extends JpaRepository<UserEntity, Integer> {
}
