package krilovs.andrejs.app.user.active;

import java.time.LocalDateTime;
import krilovs.andrejs.app.user.UserEntity;
import krilovs.andrejs.app.user.UserRole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ActiveUserMapperTest {
  @InjectMocks
  ActiveUserMapperImpl activeUserMapper;

  @Test
  void shouldMapAllFieldsWhenEntityIsValid() {
    var entity = new UserEntity();
    entity.setId(1L);
    entity.setEmail("entity@example.com");
    entity.setRole(UserRole.CUSTOMER);
    entity.setEnabled(Boolean.FALSE);

    var activeUser = new ActiveUserEntity();
    activeUser.setUser(entity);
    activeUser.setLoggedIn(LocalDateTime.of(2025, 9, 9, 20, 45));

    var dto = activeUserMapper.toUserDto(activeUser);
    Assertions.assertNotNull(dto);
    Assertions.assertEquals(dto.getId(), entity.getId());
    Assertions.assertEquals(dto.getEmail(), entity.getEmail());
    Assertions.assertEquals(dto.getRole(), entity.getRole());
    Assertions.assertEquals(dto.getEnabled(), entity.getEnabled());
    Assertions.assertNotNull(dto.getLoggedIn());
    Assertions.assertEquals(LocalDateTime.of(2025, 9, 9, 20, 45), dto.getLoggedIn());
  }

  @Test
  void shouldReturnNullWhenEntityIsNull() {
    Assertions.assertNull(activeUserMapper.toUserDto(null));
  }

  @Test
  void shouldReturnDtoWithNullsWhenUserIsNull() {
    var activeUser = new ActiveUserEntity();
    activeUser.setUser(null);
    activeUser.setLoggedIn(LocalDateTime.of(2025, 9, 9, 21, 0));

    var dto = activeUserMapper.toUserDto(activeUser);
    Assertions.assertNotNull(dto);
    Assertions.assertNull(dto.getId());
    Assertions.assertNull(dto.getEmail());
    Assertions.assertNull(dto.getRole());
    Assertions.assertNull(dto.getEnabled());
    Assertions.assertNotNull(dto.getLoggedIn());
    Assertions.assertEquals(LocalDateTime.of(2025, 9, 9, 21, 0), dto.getLoggedIn());
  }
}