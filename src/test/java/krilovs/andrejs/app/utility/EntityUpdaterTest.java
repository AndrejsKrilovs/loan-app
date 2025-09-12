package krilovs.andrejs.app.utility;

import krilovs.andrejs.app.exception.ApplicationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EntityUpdaterTest {
  static class TestEntity {
    String firstName;
    String lastName;
    LocalDate birthDate;
    String phone;

    TestEntity(String firstName, String lastName, LocalDate birthDate, String phone) {
      this.firstName = firstName;
      this.lastName = lastName;
      this.birthDate = birthDate;
      this.phone = phone;
    }
  }

  @Test
  void shouldUpdateDifferentStringIgnoringCaseAndSpaces() {
    var source = new TestEntity(" John", "DOE", LocalDate.of(1990, 1, 1), "123");
    var target = new TestEntity("John", "DOE ", LocalDate.of(1990, 1, 1), "123");

    EntityUpdater.updateFields(source, target);

    assertEquals("John", target.firstName);
    assertEquals("DOE", target.lastName);
  }

  @Test
  void shouldNotUpdateIfValuesAreEqual() {
    var source = new TestEntity("Alice", "Smith", LocalDate.of(2000, 5, 5), "555");
    var target = new TestEntity("Alice", "Smith", LocalDate.of(2000, 5, 5), "555");

    EntityUpdater.updateFields(source, target);

    assertEquals("Alice", target.firstName);
    assertEquals("Smith", target.lastName);
    assertEquals(LocalDate.of(2000, 5, 5), target.birthDate);
  }

  @Test
  void shouldUpdateNonStringFields() {
    var source = new TestEntity("A", "B", LocalDate.of(1995, 3, 3), "111");
    var target = new TestEntity("A", "B", LocalDate.of(2000, 1, 1), "222");

    EntityUpdater.updateFields(source, target);

    assertEquals(LocalDate.of(1995, 3, 3), target.birthDate);
    assertEquals("111", target.phone);
  }

  @Test
  void shouldDoNothingIfSourceIsNull() {
    var target = new TestEntity("X", "Y", LocalDate.of(2010, 10, 10), "999");
    var exception = assertThrows(ApplicationException.class, () -> EntityUpdater.updateFields(null, target));

    assertEquals(HttpStatus.NOT_MODIFIED, exception.getStatus());
    assertEquals("Not update fields. Entities cannot be null", exception.getMessage());
  }
}