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
  String message() default "Personal code must be in 'ddMMyyyy-nnnnn' format";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
