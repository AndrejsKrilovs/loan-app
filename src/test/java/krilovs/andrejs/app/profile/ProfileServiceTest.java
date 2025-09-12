package krilovs.andrejs.app.profile;

import krilovs.andrejs.app.exception.ApplicationException;
import krilovs.andrejs.app.user.UserEntity;
import krilovs.andrejs.app.user.UserRepository;
import krilovs.andrejs.app.user.UserRole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {
  @Mock
  private ProfileMapper profileMapper;

  @Mock
  private UserRepository userRepository;

  @Mock
  private ProfileRepository profileRepository;

  @InjectMocks
  private ProfileService profileService;

  private ProfileDto dto;
  private UserEntity userEntity;
  private ProfileEntity profileEntity;

  @BeforeEach
  void setUp() {
    dto = ProfileDto.builder()
      .userId(1L)
      .firstName("Andrejs")
      .lastName("Krilovs")
      .personalCode("10092025-12345")
      .birthDate(LocalDate.of(2025, 9, 10))
      .phone("+37121234567")
      .bankCardNumber("1234 5678 9012 3456")
      .build();

    userEntity = new UserEntity();
    userEntity.setId(1L);
    userEntity.setEmail("test@example.com");
    userEntity.setPassword("hashed");
    userEntity.setRole(UserRole.CUSTOMER);
    userEntity.setEnabled(Boolean.TRUE);

    profileEntity = new ProfileEntity();
    profileEntity.setUser(userEntity);
    profileEntity.setFirstName("Andrejs");
    profileEntity.setLastName("Krilovs");
    profileEntity.setPersonalCode("10092025-12345");
    profileEntity.setBirthDate(LocalDate.of(2025, 9, 10));
    profileEntity.setPhone("+37121234567");
    profileEntity.setBankCardNumber("1234 5678 9012 3456");
  }

  @Test
  void shouldSaveProfile() {
    Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
    Mockito.when(profileMapper.toEntity(dto)).thenReturn(profileEntity);
    Mockito.when(profileMapper.toDto(profileEntity)).thenReturn(dto);
    Mockito.when(profileRepository.save(profileEntity)).thenReturn(profileEntity);

    var result = profileService.saveProfile(dto);
    Assertions.assertNotNull(result);
    Assertions.assertEquals(dto, result);
  }

  @Test
  void shouldThrowExceptionWhenUserNotFound() {
    Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

    var ex = Assertions.assertThrows(ApplicationException.class, () -> profileService.saveProfile(dto));
    Assertions.assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    Assertions.assertTrue(ex.getMessage().contains("not found"));
  }

  @Test
  void shouldSelectProfile() {
    Mockito.when(profileRepository.findById(1L)).thenReturn(Optional.of(profileEntity));
    Mockito.when(profileMapper.toDto(profileEntity)).thenReturn(dto);

    var result = profileService.selectProfile(1L);
    Assertions.assertNotNull(result);
    Assertions.assertEquals(dto, result);
  }

  @Test
  void shouldThrowExceptionWhenProfileNotFound() {
    Mockito.when(profileRepository.findById(1L)).thenReturn(Optional.empty());

    var ex = Assertions.assertThrows(ApplicationException.class, () -> profileService.selectProfile(1L));
    Assertions.assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    Assertions.assertEquals("Profile not found for current user", ex.getMessage());
  }

  @Test
  void shouldUpdateExistingProfile() {
    var updatedDto = ProfileDto.builder()
      .userId(1L)
      .firstName("UpdatedName")
      .lastName("Krilovs")
      .personalCode("10092025-12345")
      .birthDate(LocalDate.of(2025, 9, 10))
      .phone("+37199999999")
      .bankCardNumber("1234 5678 9012 3456")
      .build();

    var updatedEntity = new ProfileEntity();
    updatedEntity.setUser(userEntity);
    updatedEntity.setFirstName("UpdatedName");
    updatedEntity.setLastName("Krilovs");
    updatedEntity.setPersonalCode("10092025-12345");
    updatedEntity.setBirthDate(LocalDate.of(2025, 9, 10));
    updatedEntity.setPhone("+37199999999");
    updatedEntity.setBankCardNumber("1234 5678 9012 3456");

    Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
    Mockito.when(profileMapper.toEntity(updatedDto)).thenReturn(updatedEntity);
    Mockito.when(profileRepository.findById(1L)).thenReturn(Optional.of(profileEntity));
    Mockito.when(profileRepository.save(profileEntity)).thenReturn(updatedEntity);
    Mockito.when(profileMapper.toDto(updatedEntity)).thenReturn(updatedDto);

    var result = profileService.saveProfile(updatedDto);
    Assertions.assertNotNull(result);
    Assertions.assertEquals("UpdatedName", result.getFirstName());
    Assertions.assertEquals("+37199999999", result.getPhone());
    Assertions.assertEquals(updatedDto, result);
  }
}