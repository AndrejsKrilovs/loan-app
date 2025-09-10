package krilovs.andrejs.app.user.active;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ActiveUserRepository extends JpaRepository<ActiveUserEntity, Long> {
  @Modifying
  @Transactional
  @Query(
    value = """
      INSERT INTO active_user_table (active_user_id, active_user_logged_time)
      VALUES (:userId, NOW())
      """,
    nativeQuery = true
  )
  void add(@Param("userId") Long userId);

  @NonNull
  @Override
  @EntityGraph(attributePaths = {"user"})
  List<ActiveUserEntity> findAll();
}
