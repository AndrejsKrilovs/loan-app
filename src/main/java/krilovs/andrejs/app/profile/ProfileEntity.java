package krilovs.andrejs.app.profile;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import krilovs.andrejs.app.user.UserEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@ToString
@Table(name = "customer_profile_table")
public class ProfileEntity {
  @Id
  private Long id;

  @MapsId
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(
    name = "profile_user_id",
    referencedColumnName = "user_id",
    unique = true,
    nullable = false,
    foreignKey = @ForeignKey(name = "profile_user_fk")
  )
  private UserEntity user;

  @Column(
    name = "profile_name",
    nullable = false,
    length = 15
  )
  private String firstName;

  @Column(
    name = "profile_surname",
    nullable = false,
    length = 20
  )
  private String lastName;

  @Column(
    name = "profile_personal_code",
    nullable = false,
    unique = true,
    length = 15
  )
  private String personalCode;

  @Column(
    name = "profile_birth_date",
    nullable = false
  )
  private LocalDate birthDate;

  @Column(
    name = "profile_phone",
    nullable = false,
    length = 16
  )
  private String phone;

  @Column(name = "profile_address")
  private String address;

  @Column(
    name = "profile_bank_card_number",
    unique = true,
    nullable = false,
    length = 19
  )
  private String bankCardNumber;

  @Version
  @Column(name = "profile_version")
  private Integer version;
}
