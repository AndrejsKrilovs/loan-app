package krilovs.andrejs.app.user;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/loan-app/users")
public class UserController {
  private final UserService userService;

  @GetMapping
  public ResponseEntity<List<UserDto>> getActiveUsers() {
    return ResponseEntity.ok(userService.getActiveUsers());
  }

  @PostMapping("/register")
  public ResponseEntity<UserDto> registerUser(@Valid @RequestBody UserDto userToRegister) {
    var rs = ResponseEntity
      .status(HttpStatus.CREATED)
      .body(userService.registerNewUser(userToRegister));
    return rs;
  }

  @PostMapping("/login")
  public ResponseEntity<UserDto> login(@Valid @RequestBody UserDto userToLogin) {
    return ResponseEntity
      .status(HttpStatus.ACCEPTED)
      .body(userService.login(userToLogin));
  }
}
