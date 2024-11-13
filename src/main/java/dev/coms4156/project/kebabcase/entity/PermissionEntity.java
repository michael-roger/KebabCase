package dev.coms4156.project.kebabcase.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Entity detailing a permission that a client can have.
 */
@Accessors(chain = true)
@Getter
@Setter
@NoArgsConstructor()
@Entity(name = "Permission")
@Table(name = "permissions")
public class PermissionEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;

  @Column(name = "name")
  private String name;

  @Column(name = "created_datetime")
  private OffsetDateTime createdDatetime;

  @Column(name = "modified_datetime")
  private OffsetDateTime modifiedDatetime;
}
