package krilovs.andrejs.app.loan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Repository
public interface LoanRepository extends JpaRepository<LoanEntity, Long> {

  default BigDecimal calculateTotalAmount(BigDecimal principal,
                                          BigDecimal annualRate,
                                          Short periods,
                                          Boolean byDays) {
    // дневная или месячная ставка
    var ratePerPeriod = byDays
      ? annualRate.divide(BigDecimal.valueOf(365), MathContext.DECIMAL32)
      : annualRate.divide(BigDecimal.valueOf(12), MathContext.DECIMAL32);

    // коэффицент (1 + i)^n
    var rate = BigDecimal.ONE.add(ratePerPeriod, MathContext.DECIMAL32);
    var calculatedRate = rate.pow(periods, MathContext.DECIMAL32);

    // формула
    var numerator = ratePerPeriod.multiply(calculatedRate, MathContext.DECIMAL32);
    var denominator = calculatedRate.subtract(BigDecimal.ONE, MathContext.DECIMAL32);

    return principal
      .multiply(
        numerator.divide(denominator, MathContext.DECIMAL32),
        MathContext.DECIMAL32
      )
      .setScale(2, RoundingMode.HALF_UP);
  }
}