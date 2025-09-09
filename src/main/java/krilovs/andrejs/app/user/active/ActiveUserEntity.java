package krilovs.andrejs.app.user.active;

import java.time.LocalDateTime;
import krilovs.andrejs.app.user.UserEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActiveUserEntity {
  private UserEntity user;
  private LocalDateTime loggedIn;
}
