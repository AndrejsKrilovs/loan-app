package krilovs.andrejs.app.profile;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/loan-app/profiles")
public class ProfileController {
  private final ProfileService profileService;

  @GetMapping("/{id}")
  public ResponseEntity<ProfileDto> getUserProfile(@PathVariable Long id) {
    return ResponseEntity.ok(profileService.selectProfile(id));
  }

  @PutMapping
  public ResponseEntity<ProfileDto> updateUserProfile(@Valid @RequestBody ProfileDto profileToUpdate) {
    return ResponseEntity
      .status(HttpStatus.ACCEPTED)
      .body(profileService.saveProfile(profileToUpdate));
  }
}
