package krilovs.andrejs.app.user;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = {"password"})
public class UserEntity {
  private Long id;
  private String email;
  private String password;
  private UserRole role;
  private Boolean enabled;
}
