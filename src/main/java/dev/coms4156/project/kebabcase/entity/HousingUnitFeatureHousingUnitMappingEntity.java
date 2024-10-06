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

@Accessors(chain = true)
@Getter
@Setter
@NoArgsConstructor()
@Entity(name = "HousingUnitFeatureHousingUnitMapping")
@Table(name = "housing_unit_feature_housing_unit_mappings")
public class HousingUnitFeatureHousingUnitMappingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "key")
    private String key;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "housing_unit_id")
    private HousingUnitEntity housingUnit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "housing_unit_feature_id")
    private HousingUnitFeatureEntity housingUnitFeature;

    @Column(name = "created_datetime")
    private OffsetDateTime createdDatetime;

    @Column(name = "modified_datetime")
    private OffsetDateTime modifiedDatetime;
}