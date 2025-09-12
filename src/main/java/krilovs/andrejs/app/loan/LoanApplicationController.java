package krilovs.andrejs.app.loan;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/loan-app/loan-applications")
public class LoanApplicationController {
  private final LoanApplicationService loanApplicationService;

  @PostMapping
  public ResponseEntity<LoanApplicationDto> createLoanApplication(
    @Valid @RequestBody LoanApplicationDto loanApplication
  ) {
    return ResponseEntity
      .status(HttpStatus.CREATED)
      .body(loanApplicationService.createLoanApplication(loanApplication));
  }

  @GetMapping
  public ResponseEntity<List<LoanApplicationDto>> getLoanApplications(
    @RequestParam Map<String, String> params
  ) {
    return ResponseEntity.ok(loanApplicationService.getLoanApplications(params));
  }
}
