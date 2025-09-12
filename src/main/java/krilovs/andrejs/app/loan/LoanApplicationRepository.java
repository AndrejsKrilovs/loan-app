package krilovs.andrejs.app.loan;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplicationEntity, Long>,
                                                   JpaSpecificationExecutor<LoanApplicationEntity> {
  @NonNull
  @Override
  @EntityGraph(attributePaths = {"customer"})
  List<LoanApplicationEntity> findAll(Specification<LoanApplicationEntity> spec);
}
