package krilovs.andrejs.app.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@ToString(exclude = {"password"})
@Table(
  name = "user_table",
  indexes = {
    @Index(
      name = "user_email_ind",
      columnList = "user_email"
    )
  }
)
public class UserEntity {
  @Id
  @Column(name = "user_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(
    name = "user_email",
    unique = true,
    nullable = false,
    length = 30
  )
  private String email;

  @Column(
    name = "user_password",
    nullable = false
  )
  private String password;

  @ColumnDefault("'CUSTOMER'")
  @Enumerated(EnumType.STRING)
  @Column(
    name = "user_role",
    nullable = false,
    length = 10
  )
  private UserRole role;

  @ColumnDefault("true")
  @Column(
    name = "is_user_enabled",
    nullable = false
  )
  private Boolean enabled;
}
