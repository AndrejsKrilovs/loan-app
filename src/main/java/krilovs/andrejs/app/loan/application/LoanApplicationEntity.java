package krilovs.andrejs.app.loan.application;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import krilovs.andrejs.app.profile.ProfileEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@ToString(exclude = {"customer"})
@Table(
  name = "loan_application_table",
  indexes = {
    @Index(
      name = "loan_application_status_ind",
      columnList = "loan_application_status"
    ),
    @Index(
      name = "loan_application_customer_ind",
      columnList = "loan_application_customer_id, loan_application_created"
    ),
    @Index(
      name = "loan_application_created_ind",
      columnList = "loan_application_created"
    )
  }
)
public class LoanApplicationEntity {
  @Id
  @Column(name = "loan_application_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(
    fetch = FetchType.LAZY,
    optional = false
  )
  @JoinColumn(
    name = "loan_application_customer_id",
    referencedColumnName = "profile_user_id",
    foreignKey = @ForeignKey(name = "loan_customer_fk")
  )
  private ProfileEntity customer;

  @Column(
    name = "loan_application_amount",
    nullable = false,
    precision = 6,
    scale = 2
  )
  private BigDecimal amount;

  @Column(
    name = "loan_application_term",
    nullable = false
  )
  private Short termDays;

  @Column(
    name = "loan_application_percent",
    nullable = false,
    precision = 5,
    scale = 4
  )
  private BigDecimal percent;

  @ColumnDefault("'NEW'")
  @Enumerated(EnumType.STRING)
  @Column(
    name = "loan_application_status",
    nullable = false,
    length = 15
  )
  private LoanApplicationStatus status;

  @ColumnDefault("CURRENT_TIMESTAMP")
  @Column(
    name = "loan_application_created",
    nullable = false
  )
  private LocalDateTime createdAt;

  @Column(name = "loan_application_decisioned")
  private LocalDateTime decisionAt;
}
