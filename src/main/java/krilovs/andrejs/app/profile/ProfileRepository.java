package krilovs.andrejs.app.profile;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import krilovs.andrejs.app.utility.EntityUpdater;
import org.springframework.stereotype.Repository;

@Repository
public class ProfileRepository {
  private final Map<Long, ProfileEntity> profiles = new ConcurrentHashMap<>();

  public Optional<ProfileEntity> findByUserId(Long userId) {
    return Optional.ofNullable(profiles.get(userId));
  }

  public void save(ProfileEntity userProfile) {
    profiles.compute(userProfile.getUser().getId(), (userId, profile) -> {
      if (Objects.isNull(profile)) {
        return userProfile;
      }

      EntityUpdater.updateFields(userProfile, profile);
      return profile;
    });
  }
}
