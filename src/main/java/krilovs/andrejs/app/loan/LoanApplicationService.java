package krilovs.andrejs.app.loan;

import krilovs.andrejs.app.exception.ApplicationException;
import krilovs.andrejs.app.profile.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanApplicationService {
  private final ProfileRepository profileRepository;
  private final LoanApplicationMapper loanApplicationMapper;
  private final LoanApplicationRepository loanApplicationRepository;

  private static Specification<LoanApplicationEntity> buildSpecification(
    Map<String, String> params
  ) {
    return (root, query, cb) -> {
      var predicate = cb.conjunction();

      if (params.containsKey("status")) {
        predicate = cb.and(
          predicate,
          cb.equal(root.get("status"), LoanApplicationStatus.valueOf(params.get("status")))
        );
      }
      else if (params.containsKey("customerId")) {
        predicate = cb.and(
          predicate,
          cb.equal(root.get("customer").get("id"), Long.valueOf(params.get("customerId")))
        );
        Objects.requireNonNull(query).orderBy(cb.desc(root.get("createdAt")));
      }
      else if (params.containsKey("createdFrom") && params.containsKey("createdTo")) {
        var from = LocalDate.parse(params.get("createdFrom"));
        var to = LocalDate.parse(params.get("createdTo"));
        predicate = cb.and(predicate, cb.between(root.get("createdAt"), from, to));
      }
      else {
        throw new ApplicationException(
          HttpStatus.PRECONDITION_FAILED,
          "Correct parameters should be set to filter loan applications"
        );
      }
      return predicate;
    };
  }

  public List<LoanApplicationDto> getLoanApplications(Map<String, String> params) {
    log.debug("Attempting to filter loans with params {} from database", params);

    var loans = loanApplicationRepository
      .findAll(buildSpecification(params))
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
    var profile = profileRepository
      .findById(loanApplication.getCustomerId())
      .orElseThrow(() -> {
        log.warn("Profile with id={} not found", loanApplication.getCustomerId());
        return new ApplicationException(HttpStatus.NOT_FOUND, "Profile not found");
      });

    var entity = loanApplicationMapper.toEntity(loanApplication);
    entity.setCustomer(profile);
    log.debug("Attempting to save loan application {}", entity);

    var saved = loanApplicationRepository.save(entity);
    log.info("Loan {} saved successfully", saved);
    return loanApplicationMapper.toDto(saved);
  }
}
