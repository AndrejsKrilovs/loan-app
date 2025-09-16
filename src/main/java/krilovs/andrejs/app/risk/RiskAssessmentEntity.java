package krilovs.andrejs.app.risk;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import krilovs.andrejs.app.loan.application.LoanApplicationEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "risk_assessments_table")
public class RiskAssessmentEntity {
  @Id
  private Long id;

  @MapsId
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(
    name = "assessment_loan_application_id",
    referencedColumnName = "loan_application_id",
    unique = true,
    nullable = false,
    foreignKey = @ForeignKey(name = "assessment_loan_application_fk")
  )
  private LoanApplicationEntity application; 

  @Column(name = "assessment_existing_loan")
  private Boolean existingActiveLoan;

  @Column(name = "assessment_blacklist_hit")
  private Boolean blacklistHit;
}
