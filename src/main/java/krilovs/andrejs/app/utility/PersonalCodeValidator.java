package krilovs.andrejs.app.utility;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Objects;
import java.util.regex.Pattern;

public class PersonalCodeValidator implements ConstraintValidator<ValidPersonalCode, String> {
  private static final Pattern PATTERN =
    Pattern.compile("^(0[1-9]|[12][0-9]|3[01])(0[1-9]|1[0-2])(\\d{4})-(\\d{5})$");

  @Override
  public boolean isValid(String string, ConstraintValidatorContext constraintValidatorContext) {
    if (Objects.isNull(string) || string.isBlank()) {
      return Boolean.FALSE;
    }

    var matcher = PATTERN.matcher(string);
    if (!matcher.matches()) {
      return Boolean.FALSE;
    }

    var day = Integer.parseInt(matcher.group(1));
    var month = Integer.parseInt(matcher.group(2));
    var year = Integer.parseInt(matcher.group(3));

    try {
      LocalDate.of(year, month, day);
    } catch (DateTimeException e) {
      return Boolean.FALSE;
    }

    return Boolean.TRUE;
  }
}
