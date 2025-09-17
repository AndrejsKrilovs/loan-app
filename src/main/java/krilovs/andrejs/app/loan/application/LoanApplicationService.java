package krilovs.andrejs.app.loan.application;

import krilovs.andrejs.app.exception.ApplicationException;
import krilovs.andrejs.app.loan.LoanEntity;
import krilovs.andrejs.app.loan.LoanRepository;
import krilovs.andrejs.app.loan.LoanStatus;
import krilovs.andrejs.app.profile.ProfileRepository;
import krilovs.andrejs.app.risk.RiskAssessmentEntity;
import krilovs.andrejs.app.risk.RiskAssessmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanApplicationService {
  private final LoanRepository loanRepository;
  private final ProfileRepository profileRepository;
  private final LoanApplicationMapper loanApplicationMapper;
  private final RiskAssessmentRepository riskAssessmentRepository;
  private final LoanApplicationRepository loanApplicationRepository;

  private final Map<LoanApplicationStatus, Consumer<LoanApplicationEntity>> statusHandlers =
    Map.of(
      LoanApplicationStatus.UNDER_REVIEW, this::handleUnderReview,
      LoanApplicationStatus.APPROVED, this::handleApproved,
      LoanApplicationStatus.REJECTED, this::handleRejected
    );

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

  @Transactional(rollbackFor = DataIntegrityViolationException.class)
  public SimpleLoanApplicationDto changeLoanApplicationStatus(LoanApplicationStatus status, Long loanId) {
    log.debug("Attempting to change loan application status");

    var updated = loanApplicationRepository
      .update(status.name(), loanId)
      .map(loanApplication -> {
        statusHandlers.getOrDefault(
            loanApplication.getStatus(),
            l -> log.debug("No handler for status {}", l.getStatus())
          )
          .accept(loanApplication);
        return loanApplicationMapper.toSimpleDto(loanApplication);
      })
      .orElseThrow(() -> {
        log.warn("Status cannot be changed for non existing loan application with id={}", loanId);
        return new ApplicationException(
          HttpStatus.NOT_ACCEPTABLE,
          "Status '%s' cannot be changed for non existing loan application with id=%s"
            .formatted(status, loanId)
        );
      });

    log.info("Status '{}' is applied for loan application {}", status, updated);
    return updated;
  }

  private void handleUnderReview(LoanApplicationEntity loanApplication) {
    log.debug(
      "Handling loan application with status 'UNDER_REVIEW' for loanApplicationId={}",
      loanApplication.getId()
    );
    var riskAssessmentEntity = new RiskAssessmentEntity();
    riskAssessmentEntity.setApplication(loanApplication);

    try {
      riskAssessmentRepository.save(riskAssessmentEntity);
    }
    catch (DataIntegrityViolationException violationException) {
      throw new ApplicationException(
        HttpStatus.CONFLICT,
        "Loan application with id=%d already in status 'UNDER_REVIEW'".formatted(loanApplication.getId())
      );
    }
  }

  private void handleApproved(LoanApplicationEntity loanApplication) {
    log.debug(
      "Handling loan application with status 'APPROVED' for loanApplicationId={}",
      loanApplication.getId()
    );

    var loan = new LoanEntity();
    var createdAt = loanApplication.getCreatedAt().toLocalDate();
    loan.setApplication(loanApplication);
    loan.setStatus(LoanStatus.ACTIVE);
    loan.setStartDate(createdAt);
    loan.setEndDate(createdAt.plusDays(loanApplication.getTermDays().longValue()));

    var calculatedAdditionalAmount = loanRepository
      .calculateTotalAmount(
        loanApplication.getAmount(),
        loanApplication.getPercent(),
        loanApplication.getTermDays(),
        Boolean.FALSE
      );
    loan.setOutstanding(loanApplication.getAmount().add(calculatedAdditionalAmount));
    loanRepository.save(loan);
    riskAssessmentRepository.deleteById(loanApplication.getId());
  }

  private void handleRejected(LoanApplicationEntity loanApplication) {
    log.debug(
      "Handling loan application with status 'REJECTED' for loanApplicationId={}",
      loanApplication.getId()
    );
    riskAssessmentRepository.deleteById(loanApplication.getId());
  }
}
