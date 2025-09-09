package krilovs.andrejs.app.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;

@Value
@Builder
@ToString(exclude = {"password"})
public class UserDto {
  @Null(message = "User identifier should be null")
  Long id;

  @NotNull(message = "User email should be defined")
  @NotBlank(message = "User email should not be empty")
  @Email(message = "Incorrect user email")
  String email;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @NotNull(message = "User password should be defined")
  @NotBlank(message = "User password should not be empty")
  @Size(min = 8, message = "User password should be at least 8 characters")
  @Pattern(
    regexp = "^(?=.*[A-Za-zА-Яа-яЁё])(?=.*\\d)(?=.*[@#$%^&+=!()\\-_*]).{8,}$",
    message = """
      Password should contain at least:
       - one latin or cyrillic letter,
       - one digit,
       - one special character,
       - minimum 8 characters.
      """
  )
  String password;
  UserRole role;
  Boolean enabled;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  LocalDateTime loggedIn;
}
