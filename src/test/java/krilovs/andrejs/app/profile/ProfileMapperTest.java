package krilovs.andrejs.app.profile;

import krilovs.andrejs.app.user.UserEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
class ProfileMapperTest {
  @InjectMocks
  ProfileMapperImpl profileMapper;

  @Test
  void shouldMapDtoToEntity() {
    var dto = ProfileDto.builder()
      .userId(1L)
      .firstName("Andrejs")
      .lastName("Krilovs")
      .personalCode("123456-78901")
      .birthDate(LocalDate.of(2000, 1, 1))
      .phone("+37120000000")
      .address("Riga, Latvia")
      .bankCardNumber("4000123412341234")
      .build();

    var entity = profileMapper.toEntity(dto);
    Assertions.assertNotNull(entity);
    Assertions.assertEquals(dto.getFirstName(), entity.getFirstName());
    Assertions.assertEquals(dto.getLastName(), entity.getLastName());
    Assertions.assertEquals(dto.getPersonalCode(), entity.getPersonalCode());
    Assertions.assertEquals(dto.getBirthDate(), entity.getBirthDate());
    Assertions.assertEquals(dto.getPhone(), entity.getPhone());
    Assertions.assertEquals(dto.getAddress(), entity.getAddress());
    Assertions.assertEquals(dto.getBankCardNumber(), entity.getBankCardNumber());
  }

  @Test
  void shouldReturnNullWhenDtoIsNull() {
    Assertions.assertNull(profileMapper.toEntity(null));
  }

  @Test
  void shouldMapEntityToDto() {
    var user = new UserEntity();
    user.setId(2L);

    var entity = new ProfileEntity();
    entity.setUser(user);
    entity.setFirstName("Jane");
    entity.setLastName("Smith");
    entity.setPersonalCode("987654-32109");
    entity.setBirthDate(LocalDate.of(1985, 5, 20));
    entity.setPhone("+37120000001");
    entity.setAddress("Vilnius, Lithuania");
    entity.setBankCardNumber("5000432112345678");

    var dto = profileMapper.toDto(entity);
    Assertions.assertNotNull(dto);
    Assertions.assertEquals(user.getId(), dto.getUserId());
    Assertions.assertEquals(entity.getFirstName(), dto.getFirstName());
    Assertions.assertEquals(entity.getLastName(), dto.getLastName());
    Assertions.assertEquals(entity.getPersonalCode(), dto.getPersonalCode());
    Assertions.assertEquals(entity.getBirthDate(), dto.getBirthDate());
    Assertions.assertEquals(entity.getPhone(), dto.getPhone());
    Assertions.assertEquals(entity.getAddress(), dto.getAddress());
    Assertions.assertEquals(entity.getBankCardNumber(), dto.getBankCardNumber());
  }

  @Test
  void shouldReturnNullWhenEntityIsNull() {
    Assertions.assertNull(profileMapper.toDto(null));
  }

  @Test
  void shouldReturnDtoWithNullUser() {
    var entity = new ProfileEntity();
    entity.setUser(null);
    entity.setFirstName("NoUser");

    var dto = profileMapper.toDto(entity);
    Assertions.assertNotNull(dto);
    Assertions.assertNull(dto.getUserId());
    Assertions.assertEquals("NoUser", dto.getFirstName());
  }

  @Test
  void shouldReturnNull() {
    Assertions.assertNull(profileMapper.toDto(null));
  }
}