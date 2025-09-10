package krilovs.andrejs.app.utility;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PersonalCodeValidator.class)
public @interface ValidPersonalCode {
  String message() default "Invalid personal code format or invalid date";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
