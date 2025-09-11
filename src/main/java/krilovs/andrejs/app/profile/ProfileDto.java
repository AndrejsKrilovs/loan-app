package krilovs.andrejs.app.profile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import krilovs.andrejs.app.utility.ValidPersonalCode;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
@ToString
public class ProfileDto {
  @NotNull(message = "User id should be provided")
  @PositiveOrZero(message = "User identifier should be positive number")
  Long userId;

  @NotNull(message = "Name should be defined")
  @NotBlank(message = "Name should not be empty")
  @Pattern(
    regexp = "^[A-ZА-ЯЁ][a-zа-яё]+(?:-[A-ZА-ЯЁ][a-zа-яё]+)?$",
    message = "Name must be correct"
  )
  @Size(
    max = 15,
    message = "Name maximal length is 15 characters"
  )
  String firstName;

  @NotNull(message = "Surname should be defined")
  @NotBlank(message = "Surname should not be empty")
  @Pattern(
    regexp = "^[A-ZА-ЯЁ][a-zа-яё]+(?:-[A-ZА-ЯЁ][a-zа-яё]+)?(?: [A-ZА-ЯЁ][a-zа-яё]+(?:-[A-ZА-ЯЁ][a-zа-яё]+)?)?$",
    message = "Surname must be correct"
  )
  @Size(
    max = 20,
    message = "Surname maximal length is 20 characters"
  )
  String lastName;

  @NotNull(message = "Personal code should be defined")
  @NotBlank(message = "Personal code should not be empty")
  @ValidPersonalCode
  String personalCode;

  @NotNull(message = "Birthdate should be defined")
  @Past(message = "Birth date should be in past")
  LocalDate birthDate;

  @NotNull(message = "Phone number should be defined")
  @NotBlank(message = "Phone number should not be empty")
  @Pattern(
    regexp = "^\\+[1-9]\\d{1,14}$",
    message = "Phone number should be valid, including country code"
  )
  String phone;

  @NotNull(message = "Card number should be defined")
  @NotBlank(message = "Card number should not be empty")
  @Pattern(
    regexp = "^(?:\\d{4}[- ]?){3}\\d{4}$",
    message = "Card number should be valid"
  )
  String bankCardNumber;
  String address;
}
