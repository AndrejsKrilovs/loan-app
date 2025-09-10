package krilovs.andrejs.app.utility;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PersonalCodeValidatorTest {
  private PersonalCodeValidator validator;

  @BeforeEach
  void setUp() {
    validator = new PersonalCodeValidator();
  }

  @ParameterizedTest
  @ValueSource(
    strings = {
      "01012000-12345",
      "31121999-54321",
      "29022020-00001"
    }
  )
  void shouldReturnValidPersonalCode(String personalCode) {
    Assertions.assertTrue(validator.isValid(personalCode, null));
  }

  @ParameterizedTest
  @ValueSource(
    strings = {
      "01012000",
      "01-01-2000-12345",
      "010120000-12345",
      "abc",
      "32012000-12345",
      "00012000-12345",
      "01132000-12345",
      "01002000-12345",
      "29022021-11111",
      "",
      "   "
    }
  )
  void shouldReturnInvalidPersonalCode(String personalCode) {
    Assertions.assertFalse(validator.isValid(personalCode, null));
  }

  @Test
  void shouldReturnFalseWhenNull() {
    Assertions.assertFalse(validator.isValid(null, null));
  }
}