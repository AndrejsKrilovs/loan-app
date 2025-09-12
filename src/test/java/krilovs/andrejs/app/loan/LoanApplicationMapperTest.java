package krilovs.andrejs.app.loan;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class LoanApplicationMapperTest {
  @InjectMocks
  LoanApplicationMapperImpl loanApplicationMapper;

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
  void shouldReturnNullWhenDtoIsNull() {
    Assertions.assertNull(loanApplicationMapper.toEntity(null));
  }

  @Test
  void shouldReturnNullWhenEntityIsNull() {
    Assertions.assertNull(loanApplicationMapper.toDto(null));
  }

  @Test
  void shouldMapEntityToDto() {
  }

  @Test
  void shouldReturnCustomerIdWhenCustomerIsPresent() {
  }

  @Test
  void shouldReturnNullCustomerIdWhenCustomerIsNull() {
  }
}