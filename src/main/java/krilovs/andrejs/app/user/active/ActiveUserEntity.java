package krilovs.andrejs.app.user.active;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import krilovs.andrejs.app.user.UserEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@ToString
@Table(name = "active_user_table")
public class ActiveUserEntity {
  @Id
  private Long id;

  @MapsId
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(
    name = "active_user_id",
    referencedColumnName = "user_id",
    unique = true,
    nullable = false,
    foreignKey = @ForeignKey(name = "active_user_fk")
  )
  private UserEntity user;

  @ColumnDefault("CURRENT_TIMESTAMP")
  @Column(
    name = "active_user_logged_time",
    nullable = false
  )
  private LocalDateTime loggedTime;
}
