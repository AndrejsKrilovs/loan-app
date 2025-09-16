package krilovs.andrejs.app.loan.application;

import krilovs.andrejs.app.exception.ApplicationException;
import krilovs.andrejs.app.profile.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanApplicationService {
  private final ProfileRepository profileRepository;
  private final LoanApplicationMapper loanApplicationMapper;
  private final LoanApplicationRepository loanApplicationRepository;

  public List<LoanApplicationDto> getLoanApplications(Map<String, String> params) {
    log.debug("Attempting to filter loans with params {} from database", params);

    var loans = loanApplicationRepository
      .findAll(LoanApplicationSpecification.buildSelectSpecification(params))
      .stream()
      .map(loanApplicationMapper::toDto)
      .toList();

    if (loans.isEmpty()) {
      log.warn("No loan applications found in the system");
      throw new ApplicationException(HttpStatus.OK, "No loans at this moment");
    }

    log.info("Found {} loans in the system", loans.size());
    return loans;
  }

  public LoanApplicationDto createLoanApplication(LoanApplicationDto loanApplication) {
    var profile = profileRepository.findById(loanApplication.getCustomerId())
      .orElseThrow(() -> {
        log.warn("Profile with id={} not found", loanApplication.getCustomerId());
        return new ApplicationException(
          HttpStatus.NOT_FOUND, "Profile not found"
        );
      });

    var ageVerification = profile.getBirthDate().isAfter(LocalDate.now().minusYears(18L));
    if (ageVerification) {
      log.warn("To create loan application, requested person must be older than 18 years old.");
      throw new ApplicationException(
        HttpStatus.TOO_EARLY,
        "To create loan application, requested person must be older than 18 years old."
      );
    }

    var entity = loanApplicationMapper.toEntity(loanApplication);
    entity.setCustomer(profile);
    log.debug("Attempting to save loan application {}", entity);

    var saved = loanApplicationRepository.add(
        entity.getAmount(),
        entity.getPercent(),
        entity.getTermDays(),
        entity.getStatus().name(),
        entity.getCustomer().getId()
      )
      .orElseThrow(() -> {
        log.warn(
          "Cannot create loan application for user {} {}. User already have opened loan in status 'NEW' or 'UNDER_REVIEW'",
          entity.getCustomer().getFirstName(),
          entity.getCustomer().getLastName()
        );

        return new ApplicationException(
          HttpStatus.CONFLICT,
          "Cannot create loan application for user %s %s. User already have opened loan in status 'NEW' or 'UNDER_REVIEW''"
            .formatted(entity.getCustomer().getFirstName(), entity.getCustomer().getLastName())
        );
      });

    log.info("New loan application {} saved successfully", saved);
    return loanApplicationMapper.toDto(saved);
  }

  public LoanApplicationDto changeLoanApplicationStatus(LoanApplicationStatus status, Long loanId) {
    log.debug("Attempting to change loan application status");

    var updated = loanApplicationRepository
      .update(status.name(), loanId)
      .map(loanApplicationMapper::toDto)
      .orElseThrow(() -> {
        log.warn("Status cannot be changed for non existing loan application with id={}", loanId);
        return new ApplicationException(
          HttpStatus.NOT_ACCEPTABLE,
          "Status '%s' cannot be changed for non existing loan application with id=%s".formatted(status, loanId)
        );
      });

    log.info("Status '{}' is applied for loan application with id={}", status, loanId);
    return updated;
  }
}
