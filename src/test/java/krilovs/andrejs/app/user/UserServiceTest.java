package krilovs.andrejs.app.user;

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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

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
    when(activeUserRepository.findAll()).thenReturn(List.of(activeUserEntity));
    when(activeUserMapper.toUserDto(activeUserEntity)).thenReturn(userDto);

    var result = userService.getActiveUsers();
    Assertions.assertNotNull(result);
    Assertions.assertFalse(result.isEmpty());
    verify(
      activeUserRepository,
      only()
    ).findAll();
    verify(
      activeUserMapper,
      only()
    ).toUserDto(activeUserEntity);
  }

  @Test
  void shouldThrowExceptionWhenNoActiveUsers() {
    when(activeUserRepository.findAll()).thenReturn(List.of());

    var exception = assertThrows(
      ApplicationException.class,
      () -> userService.getActiveUsers()
    );
    Assertions.assertNotNull(exception);
    assertEquals(
      HttpStatus.OK,
      exception.getStatus()
    );
    assertTrue(exception.getMessage()
      .contains("No active users"));
  }

  @Test
  void shouldRegisterNewUser() {
    when(userMapper.toEntity(userDto)).thenReturn(userEntity);
    when(passwordService.hashPassword(userDto.getPassword())).thenReturn("hashedPassword");
    when(userRepository.save(userEntity)).thenReturn(userEntity);
    when(userMapper.toDto(userEntity)).thenReturn(userDto);

    var result = userService.registerNewUser(userDto);
    Assertions.assertNotNull(result);
    assertEquals(
      userDto,
      result
    );
    verify(
      passwordService,
      only()
    ).hashPassword(userDto.getPassword());
  }

  @Test
  void shouldThrowExceptionWhenTryToRegisterDuplicateUser() {
    when(userMapper.toEntity(userDto)).thenReturn(userEntity);
    when(userRepository.save(userEntity)).thenThrow(DataIntegrityViolationException.class);

    ApplicationException ex = assertThrows(
      ApplicationException.class,
      () -> userService.registerNewUser(userDto)
    );

    assertEquals(
      HttpStatus.CONFLICT,
      ex.getStatus()
    );

    assertTrue(
      ex.getMessage()
        .contains("User with email 'test@example.com' already exists"),
      "Exception message should mention duplicate email"
    );

    verify(
      userRepository,
      only()
    ).save(userEntity);
    verifyNoMoreInteractions(userRepository);

  }

  @Test
  void shouldLoginUser() {
    when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.of(userEntity));
    when(passwordService.verifyPassword(
      userDto.getPassword(),
      userEntity.getPassword()
    )).thenReturn(true);
    when(userMapper.toDto(userEntity)).thenReturn(userDto);

    var result = userService.login(userDto);
    assertEquals(
      userDto,
      result
    );
    verify(
      activeUserRepository,
      only()
    ).add(userEntity);
  }

  @Test
  void shouldThrowExceptionWhenUserNotFound() {
    when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.empty());

    var ex = assertThrows(
      ApplicationException.class,
      () -> userService.login(userDto)
    );
    assertEquals(
      HttpStatus.NOT_FOUND,
      ex.getStatus()
    );
    assertTrue(ex.getMessage()
      .contains("Invalid email"));
  }

  @Test
  void shouldThrowExceptionWhenPasswordIncorrect() {
    when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.of(userEntity));
    when(passwordService.verifyPassword(
      userDto.getPassword(),
      userEntity.getPassword()
    )).thenReturn(Boolean.FALSE);

    var exception = assertThrows(
      ApplicationException.class,
      () -> userService.login(userDto)
    );
    assertEquals(
      HttpStatus.NOT_FOUND,
      exception.getStatus()
    );
    assertTrue(exception.getMessage()
      .contains("Invalid password"));
  }

  @Test
  void shouldRemoveUserFromActiveUsers() {
    userService.logout(1L);
    verify(
      activeUserRepository,
      only()
    ).remove(1L);
  }
}
