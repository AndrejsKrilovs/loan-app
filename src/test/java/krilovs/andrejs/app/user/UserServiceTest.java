package krilovs.andrejs.app.user;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import krilovs.andrejs.app.config.PasswordService;
import krilovs.andrejs.app.exception.ApplicationException;
import krilovs.andrejs.app.user.active.ActiveUserEntity;
import krilovs.andrejs.app.user.active.ActiveUserMapper;
import krilovs.andrejs.app.user.active.ActiveUserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
  @Mock
  private UserMapper userMapper;

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordService passwordService;

  @Mock
  private ActiveUserMapper activeUserMapper;

  @Mock
  private ActiveUserRepository activeUserRepository;

  @InjectMocks
  private UserService userService;

  private UserDto userDto;
  private UserEntity userEntity;
  private ActiveUserEntity activeUserEntity;

  @BeforeEach
  void setUp() {
    userDto = UserDto.builder()
      .id(1L)
      .email("test@example.com")
      .password("Password1!")
      .role(UserRole.CUSTOMER)
      .enabled(Boolean.TRUE)
      .loggedIn(LocalDateTime.now())
      .build();

    userEntity = new UserEntity();
    userEntity.setId(1L);
    userEntity.setEmail("test@example.com");
    userEntity.setPassword("hashed");
    userEntity.setRole(UserRole.CUSTOMER);
    userEntity.setEnabled(Boolean.TRUE);

    activeUserEntity = new ActiveUserEntity();
    activeUserEntity.setUser(userEntity);
    activeUserEntity.setLoggedIn(LocalDateTime.now());
  }

  @Test
  void shouldReturnActiveUsers() {
    Mockito.when(activeUserRepository.findAll()).thenReturn(List.of(activeUserEntity));
    Mockito.when(activeUserMapper.toUserDto(activeUserEntity)).thenReturn(userDto);

    var result = userService.getActiveUsers();
    Assertions.assertNotNull(result);
    Assertions.assertFalse(result.isEmpty());
    Mockito.verify(activeUserRepository, Mockito.only()).findAll();
    Mockito.verify(activeUserMapper, Mockito.only()).toUserDto(activeUserEntity);
  }

  @Test
  void shouldThrowExceptionWhenNoActiveUsers() {
    Mockito.when(activeUserRepository.findAll()).thenReturn(List.of());

    var exception = Assertions.assertThrows(ApplicationException.class, () -> userService.getActiveUsers());
    Assertions.assertNotNull(exception);
    Assertions.assertEquals(HttpStatus.OK, exception.getStatus());
    Assertions.assertTrue(exception.getMessage().contains("No active users"));
  }

  @Test
  void shouldRegisterNewUser() {
    Mockito.when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.empty());
    Mockito.when(userMapper.toEntity(userDto)).thenReturn(userEntity);
    Mockito.when(passwordService.hashPassword(userDto.getPassword())).thenReturn("hashedPassword");
    Mockito.when(userRepository.save(userEntity)).thenReturn(userEntity);
    Mockito.when(userMapper.toDto(userEntity)).thenReturn(userDto);

    var result = userService.registerNewUser(userDto);
    Assertions.assertNotNull(result);
    Assertions.assertEquals(userDto, result);
    Mockito.verify(passwordService, Mockito.only()).hashPassword(userDto.getPassword());
  }

  @Test
  void shouldThrowExceptionWhenTryToRegisterDuplicateUser() {
    Mockito.when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.of(userEntity));

    var exception = Assertions.assertThrows(ApplicationException.class, () -> userService.registerNewUser(userDto));
    Assertions.assertNotNull(exception);
    Assertions.assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    Assertions.assertTrue(exception.getMessage().contains("already exists"));
    Mockito.verify(userRepository, Mockito.never()).save(userEntity);
    Mockito.verify(passwordService, Mockito.never()).hashPassword(userDto.getPassword());
  }

  @Test
  void shouldLoginUser() {
    Mockito.when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.of(userEntity));
    Mockito.when(passwordService.verifyPassword(userDto.getPassword(), userEntity.getPassword())).thenReturn(true);
    Mockito.when(userMapper.toDto(userEntity)).thenReturn(userDto);

    var result = userService.login(userDto);
    Assertions.assertEquals(userDto, result);
    Mockito.verify(activeUserRepository, Mockito.only()).add(userEntity);
  }

  @Test
  void shouldThrowExceptionWhenUserNotFound() {
    Mockito.when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.empty());

    var ex = Assertions.assertThrows(ApplicationException.class, () -> userService.login(userDto));
    Assertions.assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    Assertions.assertTrue(ex.getMessage().contains("Invalid email"));
  }

  @Test
  void shouldThrowExceptionWhenPasswordIncorrect() {
    Mockito.when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.of(userEntity));
    Mockito.when(passwordService.verifyPassword(userDto.getPassword(), userEntity.getPassword())).thenReturn(Boolean.FALSE);

    var exception = Assertions.assertThrows(ApplicationException.class, () -> userService.login(userDto));
    Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    Assertions.assertTrue(exception.getMessage().contains("Invalid password"));
  }

  @Test
  void shouldRemoveUserFromActiveUsers() {
    userService.logout(1L);
    Mockito.verify(activeUserRepository, Mockito.only()).remove(1L);
  }
}
