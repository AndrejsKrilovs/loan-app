package krilovs.andrejs.app.profile;

import java.time.LocalDate;
import krilovs.andrejs.app.user.UserEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ProfileEntity {
  private UserEntity user;
  private String firstName;
  private String lastName;
  private String personalCode;
  private LocalDate birthDate;
  private String phone;
  private String address;
  private String bankCardNumber;
}
