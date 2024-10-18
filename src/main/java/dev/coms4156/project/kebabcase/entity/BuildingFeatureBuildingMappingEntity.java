package dev.coms4156.project.kebabcase.entity;

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
 * Entity detailing all the information held by a mapping between
 *     buildings and their corresponding features.
 */

@Accessors(chain = true)
@Getter
@Setter
@NoArgsConstructor()
@Entity(name = "BuildingFeatureBuildingMapping")
@Table(name = "building_feature_building_mappings")
public class BuildingFeatureBuildingMappingEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "building_id")
  private BuildingEntity building;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "building_feature_id")
  private BuildingFeatureEntity buildingFeature;

  @Column(name = "created_datetime")
  private OffsetDateTime createdDatetime;

  @Column(name = "modified_datetime")
  private OffsetDateTime modifiedDatetime;
}