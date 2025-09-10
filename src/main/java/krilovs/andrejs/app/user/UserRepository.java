package krilovs.andrejs.app.user;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {
  private final Map<Long, UserEntity> users = new ConcurrentHashMap<>();
  private final AtomicLong userId = new AtomicLong(0L);

  public UserEntity save(UserEntity newUser) {
    var id = userId.getAndIncrement();
    newUser.setId(id);
    users.put(id, newUser);
    return newUser;
  }

  public Optional<UserEntity> findByEmail(String email) {
    return users.values()
      .stream()
      .filter(usr -> usr.getEmail().equalsIgnoreCase(email))
      .findAny();
  }

  public Optional<UserEntity> findById(Long id) {
    return Optional.ofNullable(users.get(id));
  }
}
