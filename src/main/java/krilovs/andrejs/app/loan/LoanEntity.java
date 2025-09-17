package krilovs.andrejs.app.loan;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@ToString
@Table(name = "loan_table")
public class LoanEntity {
  @Id
  private Long id;

  @MapsId
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(
    name = "loan_id",
    referencedColumnName = "loan_application_id",
    unique = true,
    nullable = false,
    foreignKey = @ForeignKey(name = "loan_application_fk")
  )
  private LoanApplicationEntity application;

  @ColumnDefault("'ACTIVE'")
  @Enumerated(EnumType.STRING)
  @Column(
    name = "loan_status",
    nullable = false,
    length = 10
  )
  private LoanStatus status;

  @Column(
    name = "loan_start_date",
    nullable = false
  )
  private LocalDate startDate;

  @Column(
    name = "loan_end_date",
    nullable = false
  )
  private LocalDate endDate;

  @Column(
    name = "loan_outstanding_amount",
    nullable = false,
    precision = 6,
    scale = 2
  )
  private BigDecimal outstanding;
}
