package krilovs.andrejs.app.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PasswordServiceTest {
  @InjectMocks
  PasswordService passwordService;

  @Test
  void returnHashedValue() {
    var plainPassword = "mySecret123";
    var hashedPassword = passwordService.hashPassword(plainPassword);

    assertNotNull(hashedPassword);
    assertNotEquals(plainPassword, hashedPassword);
    assertTrue(hashedPassword.startsWith("$2"));
  }

  @Test
  void passwordMatches() {
    var plainPassword = "mySecret123";
    var hashedPassword = passwordService.hashPassword(plainPassword);

    assertTrue(passwordService.verifyPassword(plainPassword, hashedPassword));
  }

  @Test
  void incorrectHashedPassword() {
    var plainPassword = "mySecret123";
    var wrongPassword = "wrongPass";
    var hashedPassword = passwordService.hashPassword(plainPassword);

    assertFalse(passwordService.verifyPassword(wrongPassword, hashedPassword));
  }
}