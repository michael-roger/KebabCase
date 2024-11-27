package dev.coms4156.project.kebabcase.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Entity detailing all the information held by a housing unit.
 */

@Accessors(chain = true)
@Getter
@Setter
@NoArgsConstructor()
@Entity(name = "HousingUnit")
@Table(name = "housing_units")
public class HousingUnitEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "building_id")
  private BuildingEntity building;

  @Column(name = "unit_number")
  private String unitNumber;

  @Column(name = "created_datetime")
  private OffsetDateTime createdDatetime;

  @Column(name = "modified_datetime")
  private OffsetDateTime modifiedDatetime;
}
