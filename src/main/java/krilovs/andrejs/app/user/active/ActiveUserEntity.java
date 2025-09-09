package krilovs.andrejs.app.user.active;

import java.time.LocalDateTime;
import krilovs.andrejs.app.user.UserEntity;
import krilovs.andrejs.app.user.UserRole;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
public class ActiveUserEntity {
  private UserEntity user;
  private LocalDateTime loggedIn;
}
