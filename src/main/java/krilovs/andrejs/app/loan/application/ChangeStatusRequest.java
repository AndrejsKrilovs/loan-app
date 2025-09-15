package krilovs.andrejs.app.loan.application;

import jakarta.validation.constraints.NotNull;

public record ChangeStatusRequest(
  @NotNull(message = "Loan application id should be defined") Long id,
  @NotNull(message = "Loan application status should be defined") LoanApplicationStatus status
) {
}
