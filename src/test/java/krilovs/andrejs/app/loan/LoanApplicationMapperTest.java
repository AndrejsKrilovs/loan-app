package krilovs.andrejs.app.loan;

import krilovs.andrejs.app.profile.ProfileDto;
import krilovs.andrejs.app.profile.ProfileEntity;
import krilovs.andrejs.app.profile.ProfileMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class LoanApplicationMapperTest {
  @InjectMocks
  LoanApplicationMapperImpl loanApplicationMapper;

  @Mock
  ProfileMapper profileMapper;

  @Test
  void shouldMapDtoToEntity() {
    var dto = LoanApplicationDto.builder()
      .id(1L)
      .amount(BigDecimal.valueOf(1000))
      .termDays(BigDecimal.valueOf(30L))
      .percent(BigDecimal.valueOf(0.3))
      .status(LoanApplicationStatus.NEW)
      .createdAt(LocalDateTime.of(2025, 9, 1, 10, 0))
      .decisionAt(LocalDateTime.of(2025, 9, 5, 12, 0))
      .build();

    var entity = loanApplicationMapper.toEntity(dto);
    Assertions.assertNotNull(entity);
    Assertions.assertEquals(1L, entity.getId());
    Assertions.assertEquals(BigDecimal.valueOf(1000), entity.getAmount());
    Assertions.assertEquals(BigDecimal.valueOf(30L), entity.getTermDays());
    Assertions.assertEquals(BigDecimal.valueOf(0.3), entity.getPercent());
    Assertions.assertEquals(LoanApplicationStatus.NEW, entity.getStatus());
    Assertions.assertEquals(LocalDateTime.of(2025, 9, 1, 10, 0), entity.getCreatedAt());
    Assertions.assertEquals(LocalDateTime.of(2025, 9, 5, 12, 0), entity.getDecisionAt());
  }

  @Test
  void shouldSetDefaultCreatedAtAndStatusAndMapToEntity() {
    var beforeCall = LocalDateTime.now();

    var dto = LoanApplicationDto.builder()
      .id(2L)
      .amount(BigDecimal.valueOf(500))
      .termDays(BigDecimal.valueOf(15))
      .percent(BigDecimal.valueOf(0.1))
      .status(null)
      .createdAt(null)
      .decisionAt(null)
      .build();

    var entity = loanApplicationMapper.toEntity(dto);
    var afterCall = LocalDateTime.now();
    Assertions.assertNotNull(entity);
    Assertions.assertEquals(2L, entity.getId());
    Assertions.assertEquals(BigDecimal.valueOf(500), entity.getAmount());
    Assertions.assertEquals(BigDecimal.valueOf(15), entity.getTermDays());
    Assertions.assertEquals(BigDecimal.valueOf(0.1), entity.getPercent());
    Assertions.assertEquals(LoanApplicationStatus.NEW, entity.getStatus());

    Assertions.assertNotNull(entity.getCreatedAt());
    Assertions.assertFalse(entity.getCreatedAt().isBefore(beforeCall));
    Assertions.assertFalse(entity.getCreatedAt().isAfter(afterCall));
    Assertions.assertNull(entity.getDecisionAt());
  }

  @Test
  void shouldReturnNullWhenDtoIsNull() {
    Assertions.assertNull(loanApplicationMapper.toEntity(null));
  }

  @Test
  void shouldReturnNullWhenEntityIsNull() {
    Assertions.assertNull(loanApplicationMapper.toDto(null));
  }

  @Test
  void shouldMapEntityToDto() {
    var customer = new ProfileEntity();
    customer.setId(5L);

    var entity = new LoanApplicationEntity();
    entity.setId(1L);
    entity.setAmount(BigDecimal.valueOf(1000));
    entity.setTermDays(BigDecimal.valueOf(30));
    entity.setPercent(BigDecimal.valueOf(0.3));
    entity.setStatus(LoanApplicationStatus.NEW);
    entity.setCreatedAt(LocalDateTime.of(2025, 9, 1, 10, 0));
    entity.setDecisionAt(LocalDateTime.of(2025, 9, 5, 12, 0));
    entity.setCustomer(customer);

    var profileDto = ProfileDto.builder().build();
    Mockito.when(profileMapper.toDto(customer)).thenReturn(profileDto);

    var dto = loanApplicationMapper.toDto(entity);
    Assertions.assertNotNull(dto);
    Assertions.assertEquals(1L, dto.getId());
    Assertions.assertEquals(BigDecimal.valueOf(1000), dto.getAmount());
    Assertions.assertEquals(BigDecimal.valueOf(30), dto.getTermDays());
    Assertions.assertEquals(BigDecimal.valueOf(0.3), dto.getPercent());
    Assertions.assertEquals(LoanApplicationStatus.NEW, dto.getStatus());
    Assertions.assertEquals(LocalDateTime.of(2025, 9, 1, 10, 0), dto.getCreatedAt());
    Assertions.assertEquals(LocalDateTime.of(2025, 9, 5, 12, 0), dto.getDecisionAt());
    Assertions.assertEquals(5L, dto.getCustomerId());
    Assertions.assertNotNull(dto.getProfile());
    Assertions.assertEquals(profileDto, dto.getProfile());
  }

  @Test
  void shouldReturnCustomerIdWhenCustomerIsPresent() {
    var customer = new ProfileEntity();
    customer.setId(10L);

    var entity = new LoanApplicationEntity();
    entity.setCustomer(customer);
    Mockito.when(profileMapper.toDto(customer)).thenReturn(ProfileDto.builder().build());

    var dto = loanApplicationMapper.toDto(entity);
    Assertions.assertEquals(10L, dto.getCustomerId());
  }

  @Test
  void shouldReturnNullCustomerIdWhenCustomerIsNull() {
    var entity = new LoanApplicationEntity();
    entity.setCustomer(null);

    var dto = loanApplicationMapper.toDto(entity);
    Assertions.assertNull(dto.getCustomerId());
    Assertions.assertNull(dto.getProfile());
  }
}