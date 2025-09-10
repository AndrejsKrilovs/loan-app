package krilovs.andrejs.app.profile;

import krilovs.andrejs.app.exception.ApplicationException;
import krilovs.andrejs.app.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService {
  private final ProfileMapper profileMapper;
  private final UserRepository userRepository;
  private final ProfileRepository profileRepository;

  public ProfileDto saveProfile(ProfileDto profileToUpdate) {
    log.debug("Attempting to save user profile");

    var user = userRepository.findById(profileToUpdate.getUserId())
      .orElseThrow(() -> {
        log.warn("User with id={} not found", profileToUpdate.getUserId());
        return new ApplicationException(HttpStatus.NOT_FOUND, "User not found");
      });

    var entity = profileMapper.toEntity(profileToUpdate);
    entity.setUser(user);
    log.debug("Attempting to save user profile {}", entity);

    profileRepository.save(entity);
    log.info("Users profile {} updated successfully", entity);
    return profileMapper.toDto(entity);
  }

  public ProfileDto selectProfile(Long userId) {
    log.debug("Attempting to select profile for user={}", userId);

    var userProfile = profileRepository.findByUserId(userId)
      .orElseThrow(() -> {
        log.warn("Profile not found for user with id={}", userId);
        return new ApplicationException(HttpStatus.NOT_FOUND, "Profile not found for current user");
      });

    log.info("Found user profile {}", userProfile);
    return profileMapper.toDto(userProfile);
  }
}
