package krilovs.andrejs.app.risk;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RiskAssessmentRepository extends JpaRepository<RiskAssessmentEntity, Long> {
  @Override
  @Modifying
  @Transactional
  @Query(
    value = """
      DELETE
      FROM risk_assessments_table
      WHERE assessment_loan_application_id = :id
      """,
    nativeQuery = true
  )
  void deleteById(@NonNull @Param("id") Long id);
}
