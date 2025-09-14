package krilovs.andrejs.app.loan;

import krilovs.andrejs.app.exception.ApplicationException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

public class LoanApplicationSpecification {
  public static Specification<LoanApplicationEntity> buildSelectSpecification(
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
}
