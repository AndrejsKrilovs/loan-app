package krilovs.andrejs.app.user.active;

import krilovs.andrejs.app.user.UserEntity;
import krilovs.andrejs.app.user.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

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
    activeUser.setLoggedTime(LocalDateTime.of(
      2025,
      9,
      9,
      20,
      45
    ));

    var dto = activeUserMapper.toUserDto(activeUser);
    assertNotNull(dto);
    assertEquals(
      dto.getId(),
      entity.getId()
    );
    assertEquals(
      dto.getEmail(),
      entity.getEmail()
    );
    assertEquals(
      dto.getRole(),
      entity.getRole()
    );
    assertEquals(
      dto.getEnabled(),
      entity.getEnabled()
    );
    assertNotNull(dto.getLoggedTime());
    assertEquals(
      LocalDateTime.of(
        2025,
        9,
        9,
        20,
        45
      ),
      dto.getLoggedTime()
    );
  }

  @Test
  void shouldReturnNullWhenEntityIsNull() {
    assertNull(activeUserMapper.toUserDto(null));
  }

  @Test
  void shouldReturnDtoWithNullsWhenUserIsNull() {
    var activeUser = new ActiveUserEntity();
    activeUser.setUser(null);
    activeUser.setLoggedTime(LocalDateTime.of(
      2025,
      9,
      9,
      21,
      0
    ));

    var dto = activeUserMapper.toUserDto(activeUser);
    assertNotNull(dto);
    assertNull(dto.getId());
    assertNull(dto.getEmail());
    assertNull(dto.getRole());
    assertNull(dto.getEnabled());
    assertNotNull(dto.getLoggedTime());
    assertEquals(
      LocalDateTime.of(
        2025,
        9,
        9,
        21,
        0
      ),
      dto.getLoggedTime()
    );
  }
}
