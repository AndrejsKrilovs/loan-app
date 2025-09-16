package krilovs.andrejs.app.utility;

import krilovs.andrejs.app.exception.ApplicationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EntityUpdaterTest {
  @Test
  void shouldNotUpdateIfValuesAreEqualIgnoringCaseAndSpaces() {
    var source = new TestEntity(" john ", "DOE", LocalDate.of(1990, 1, 1), "123");
    var target = new TestEntity("John", "DOE", LocalDate.of(1990, 1, 1), "123");

    EntityUpdater.updateFields(source, target);
    assertEquals("John", target.firstName, "Строки должны остаться без изменений");
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
  void shouldNotUpdateIfNewValueIsNull() {
    var source = new TestEntity(null, "Smith", null, null);
    var target = new TestEntity("Alice", "Smith", LocalDate.of(2000, 5, 5), "555");

    EntityUpdater.updateFields(source, target);
    assertEquals("Alice", target.firstName);
    assertEquals("Smith", target.lastName);
    assertEquals(LocalDate.of(2000, 5, 5), target.birthDate);
    assertEquals("555", target.phone);
  }

  @Test
  void shouldIgnoreFieldNamedUser() {
    var source = new TestEntity("X", "Y", LocalDate.of(2010, 1, 1), "999");
    source.user = "newUser";

    var target = new TestEntity("X", "Y", LocalDate.of(2010, 1, 1), "999");
    target.user = "oldUser";

    EntityUpdater.updateFields(source, target);
    assertEquals("oldUser", target.user, "Поле 'user' должно игнорироваться");
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
  void shouldThrowExceptionWhenSourceIsNull() {
    var target = new TestEntity("X", "Y", LocalDate.of(2010, 10, 10), "999");
    var exception = assertThrows(ApplicationException.class,
      () -> EntityUpdater.updateFields(null, target)
    );

    assertEquals(HttpStatus.NOT_MODIFIED, exception.getStatus());
    assertEquals("Not update fields. Entities cannot be null", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenTargetIsNull() {
    var source = new TestEntity("A", "B", LocalDate.of(2000, 1, 1), "123");
    var exception = assertThrows(ApplicationException.class,
      () -> EntityUpdater.updateFields(source, null)
    );

    assertEquals(HttpStatus.NOT_MODIFIED, exception.getStatus());
    assertEquals("Not update fields. Entities cannot be null", exception.getMessage());
  }

  static class TestEntity {
    String firstName;
    String lastName;
    LocalDate birthDate;
    String phone;
    String user; // поле, которое должно игнорироваться

    TestEntity(String firstName, String lastName, LocalDate birthDate, String phone) {
      this.firstName = firstName;
      this.lastName = lastName;
      this.birthDate = birthDate;
      this.phone = phone;
    }
  }
}
