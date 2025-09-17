package krilovs.andrejs.app.loan;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

class LoanRepositoryTest {
  private final LoanRepository loanRepository = Mockito.mock(LoanRepository.class, Mockito.CALLS_REAL_METHODS);

  @Test
  void shouldCalculateTotalAmountByMonths() {
    var principal = BigDecimal.valueOf(1000);
    var annualRate = BigDecimal.valueOf(0.12); // 12% годовых
    short periods = 12;

    var result = loanRepository.calculateTotalAmount(principal, annualRate, periods, Boolean.FALSE);
    Assertions.assertNotNull(result);
    Assertions.assertEquals(BigDecimal.valueOf(88.85), result);
  }

  @Test
  void shouldCalculateTotalAmountByDays() {
    var principal = BigDecimal.valueOf(1000);
    var annualRate = BigDecimal.valueOf(0.12); // 12%
    short periods = 30;

    var result = loanRepository.calculateTotalAmount(principal, annualRate, periods, Boolean.TRUE);
    Assertions.assertEquals(BigDecimal.valueOf(33.48), result);
  }
}