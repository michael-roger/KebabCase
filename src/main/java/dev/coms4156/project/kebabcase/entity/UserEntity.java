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
 * Entity detailing all the information held by a user.
 */

@Accessors(chain = true)
@Getter
@Setter
@NoArgsConstructor()
@Entity(name = "User")
@Table(name = "users")
public class UserEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  @Column(name = "email_address")
  private String emailAddress;
    
  @Column(name = "password")
  private String password;

  @Column(name = "created_datetime")
  private OffsetDateTime createdDatetime;

  @Column(name = "modified_datetime")
  private OffsetDateTime modifiedDatetime;
}
