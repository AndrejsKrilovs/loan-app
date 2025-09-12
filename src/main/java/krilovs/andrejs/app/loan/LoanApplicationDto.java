package krilovs.andrejs.app.loan;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import krilovs.andrejs.app.profile.ProfileDto;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
@Builder
public class LoanApplicationDto {
  @Null(message = "Loan identifier should be null. It will de mapped automatically")
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
  @DecimalMin(value = "1", message = "Term should be at least 1 day")
  @DecimalMax(value = "999", message = "Term should not exceed 999 days")
  @Digits(integer = 3, fraction = 0, message = "Term days must have up to 3 integer digits only")
  BigDecimal termDays;

  @NotNull(message = "Apr percent should be defined")
  @Positive(message = "Apr percent should be positive")
  @DecimalMax(value = "1.0000", message = "Apr should not exceed 1")
  @Digits(
    integer = 1,
    fraction = 4,
    message = "Apr percent must have up to 1 integer and 4 fractional digits"
  )
  BigDecimal percent;
  LoanApplicationStatus status;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  LocalDateTime createdAt;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  LocalDateTime decisionAt;

  @Null(message = "Profile should be null. It will de mapped automatically")
  ProfileDto profile;
}
