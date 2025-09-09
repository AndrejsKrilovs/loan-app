package krilovs.andrejs.app.user.active;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import krilovs.andrejs.app.user.UserEntity;
import org.springframework.stereotype.Repository;

@Repository
public class ActiveUserRepository {
  private final Map<Long, ActiveUserEntity> activeUsers = new ConcurrentHashMap<>();

  public List<ActiveUserEntity> findAll() {
    return activeUsers
      .values()
      .stream()
      .toList();
  }

  public void add(UserEntity newUser) {
    ActiveUserEntity activeUser = new ActiveUserEntity();
    activeUser.setUser(newUser);
    activeUser.setLoggedIn(LocalDateTime.now());
    activeUsers.put(newUser.getId(), activeUser);
  }

  public void remove(Long id) {
    activeUsers.remove(id);
  }
}
