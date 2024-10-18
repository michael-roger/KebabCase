package dev.coms4156.project.kebabcase.repository;

import dev.coms4156.project.kebabcase.entity.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("TokenRepository")
public interface TokenRepositoryInterface extends JpaRepository<TokenEntity, Integer> {
}
