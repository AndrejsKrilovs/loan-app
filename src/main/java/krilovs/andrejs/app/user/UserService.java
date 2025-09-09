package krilovs.andrejs.app.user;

import java.util.List;
import krilovs.andrejs.app.config.PasswordService;
import krilovs.andrejs.app.exception.ApplicationException;
import krilovs.andrejs.app.user.active.ActiveUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
  private final UserMapper userMapper;
  private final UserRepository userRepository;
  private final PasswordService passwordService;
  private final ActiveUserRepository activeUserRepository;

  public List<UserDto> getActiveUsers() {
    log.debug("Attempting to fetch active users from database");

    var users = activeUserRepository.findAll()
      .stream()
      .map(userMapper::toDto)
      .toList();

    if (users.isEmpty()) {
      log.warn("No users found in the system");
      throw new ApplicationException(HttpStatus.OK, "No active users at this moment");
    }

    log.info("Found {} users in the system", users.size());
    return users;
  }

  public UserDto registerNewUser(UserDto newUser) {
    log.debug("Attempting to register new user");

    userRepository
      .findByEmail(newUser.getEmail())
      .ifPresent(entity -> {
        log.warn("Attempt to register user with already existing email={}", entity.getEmail());
        throw new ApplicationException(HttpStatus.CONFLICT, "User with email '%s' already exists in system.".formatted(entity.getEmail()));
      });

    var entity = userMapper.toEntity(newUser);
    entity.setEnabled(Boolean.TRUE);
    entity.setPassword(passwordService.hashPassword(newUser.getPassword()));
    log.debug("Attempting to register user with email={}", entity.getEmail());

    var saved = userRepository.save(entity);
    log.info("User {} registered successfully", saved);
    return userMapper.toDto(saved);
  }

  public UserDto login(UserDto userToLogin) {
    log.debug("Attempting to login with email={}", userToLogin.getEmail());

    var user = userRepository.findByEmail(userToLogin.getEmail())
      .orElseThrow(() -> {
        log.warn("Login attempt with non-existing email={}", userToLogin.getEmail());
        return new ApplicationException(HttpStatus.NOT_FOUND, "Invalid email");
      });

    if (!passwordService.verifyPassword(userToLogin.getPassword(), user.getPassword())) {
      log.warn("Login attempt with invalid password");
      throw new ApplicationException(HttpStatus.NOT_FOUND, "Invalid password");
    }

    log.info("User logged in successfully with email={}", user.getEmail());
    activeUserRepository.add(user);
    return userMapper.toDto(user);
  }

  public void logout(Long userId) {
    log.debug("Attempting to logout user with id={}", userId);
    activeUserRepository.remove(userId);
    log.info("User with id {} logged out", userId);
  }
}
