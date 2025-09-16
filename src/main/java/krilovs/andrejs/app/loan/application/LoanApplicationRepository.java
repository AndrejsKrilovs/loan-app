package krilovs.andrejs.app.loan.application;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplicationEntity, Long>,
                                                   JpaSpecificationExecutor<LoanApplicationEntity> {
  @NonNull
  @Override
  @EntityGraph(attributePaths = {"customer"})
  List<LoanApplicationEntity> findAll(Specification<LoanApplicationEntity> spec);

  @Transactional
  @Query(
    value = """
      UPDATE loan_application_table
      SET loan_application_status = :loanStatus,
          loan_application_decisioned = CASE
            WHEN :loanStatus IN ('APPROVED', 'REJECTED') THEN NOW()
            ELSE NULL
          END
      WHERE loan_application_id = :loanId
      RETURNING *
      """,
    nativeQuery = true
  )
  Optional<LoanApplicationEntity> update(@Param("loanStatus") String loanStatus,
                                         @Param("loanId") Long loanId);

  @Transactional
  @Query(
    value = """
      MERGE INTO loan_application_table lat
      USING (VALUES (:customerId)) AS la(loan_application_customer_id)
        ON lat.loan_application_customer_id = la.loan_application_customer_id
        AND lat.loan_application_status IN ('NEW', 'UNDER_REVIEW')
      WHEN MATCHED THEN DO NOTHING
      WHEN NOT MATCHED THEN
        INSERT (loan_application_amount,
                loan_application_percent,
                loan_application_term,
                loan_application_status,
                loan_application_customer_id)
        VALUES (:amount, :percent, :term, :loanStatus, :customerId)
        RETURNING lat.*
      """,
    nativeQuery = true
  )
  Optional<LoanApplicationEntity> add(@Param("amount") BigDecimal amount,
                                      @Param("percent") BigDecimal percent,
                                      @Param("term") Short term,
                                      @Param("loanStatus") String loanStatus,
                                      @Param("customerId") Long customerId
  );
}