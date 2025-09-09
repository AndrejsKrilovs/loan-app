package krilovs.andrejs.app.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {
  @InjectMocks
  UserMapperImpl userMapper;

  @Test
  void shouldMapAllFieldsWhenDtoIsValid() {
    UserDto dto = UserDto.builder()
      .id(1L)
      .email("test@example.com")
      .password("securePass123!")
      .role(UserRole.ADMIN)
      .enabled(Boolean.TRUE)
      .build();

    var entity = userMapper.toEntity(dto);
    Assertions.assertNotNull(entity);
    Assertions.assertEquals(dto.getId(), entity.getId());
    Assertions.assertEquals(dto.getEmail(), entity.getEmail());
    Assertions.assertEquals(dto.getPassword(), entity.getPassword());
    Assertions.assertEquals(dto.getRole(), entity.getRole());
    Assertions.assertTrue(entity.getEnabled());
  }

  @Test
  void shouldSetDefaultsWhenEnabledAndRoleAreNull() {
    UserDto dto = UserDto.builder()
      .id(2L)
      .email("default@example.com")
      .password("password123$")
      .build();

    var entity = userMapper.toEntity(dto);
    Assertions.assertNotNull(entity);
    Assertions.assertEquals(dto.getId(), entity.getId());
    Assertions.assertEquals(dto.getEmail(), entity.getEmail());
    Assertions.assertEquals(dto.getPassword(), entity.getPassword());
    Assertions.assertTrue(entity.getEnabled());
    Assertions.assertEquals(UserRole.CUSTOMER, entity.getRole());
  }

  @Test
  void shouldReturnNullWhenDtoIsNull() {
    Assertions.assertNull(userMapper.toEntity(null));
  }

  @Test
  void shouldMapAllFieldsWhenEntityIsValid() {
    UserEntity entity = new UserEntity();
    entity.setId(3L);
    entity.setEmail("entity@example.com");
    entity.setPassword("entityPass");
    entity.setRole(UserRole.CUSTOMER);
    entity.setEnabled(Boolean.FALSE);

    var dto = userMapper.toDto(entity);
    Assertions.assertNotNull(dto);
    Assertions.assertEquals(entity.getId(), dto.getId());
    Assertions.assertEquals(entity.getEmail(), dto.getEmail());
    Assertions.assertEquals(entity.getRole(), dto.getRole());
    Assertions.assertFalse(dto.getEnabled());
    Assertions.assertNull(dto.getPassword());
  }

  @Test
  void shouldReturnNullWhenEntityIsNull() {
    Assertions.assertNull(userMapper.toDto(null));
  }
}