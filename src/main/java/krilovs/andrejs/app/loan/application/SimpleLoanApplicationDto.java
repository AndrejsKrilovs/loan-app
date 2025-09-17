package krilovs.andrejs.app.loan.application;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
@Builder
public class SimpleLoanApplicationDto {
  Long id;

  @NotNull(message = "Customer should be defined")
  Long customerId;

  @NotNull(message = "Amount should be defined")
  @Positive(message = "Amount should be positive")
  @Digits(
    integer = 4,
    fraction = 2,
    message = "Amount must have up to 4 integer digits and 2 fractional digits"
  )
  @DecimalMax(value = "9999.99", message = "Amount should not exceed 9999.99")
  BigDecimal amount;

  @NotNull(message = "Term days defined")
  @Positive(message = "Term days should be positive")
  @Min(value = 1, message = "Term should be at least 1 day")
  @Max(value = 999, message = "Term should not exceed 999 days")
  Short termDays;

  @NotNull(message = "Apr percent should be defined")
  @Positive(message = "Apr percent should be positive")
  @DecimalMin(value = "1.00", message = "Minimal Apr should be 1%")
  @DecimalMax(value = "100.00", message = "Apr should not exceed 100%")
  @Digits(
    integer = 3,
    fraction = 2,
    message = "Apr percent must have up to 3 integer and 2 fractional digits"
  )
  BigDecimal percent;
  LoanApplicationStatus status;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  LocalDateTime createdAt;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  LocalDateTime decisionAt;
}
