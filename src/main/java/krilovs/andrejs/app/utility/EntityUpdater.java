package krilovs.andrejs.app.utility;

import krilovs.andrejs.app.exception.ApplicationException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Objects;

@Slf4j
@UtilityClass
public class EntityUpdater {
  /**
   * Обновляет поля target значениями из source, если они отличаются.
   * Работает для любых Entity через рефлексию.
   */
  public static <T> void updateFields(T source, T target) {
    if (Objects.isNull(source) || Objects.isNull(target)) {
      log.error("Not update fields. Entities cannot be null");
      throw new ApplicationException(
        HttpStatus.NOT_MODIFIED,
        "Not update fields. Entities cannot be null"
      );
    }

    Arrays.stream(source.getClass().getDeclaredFields())
      .filter(field -> !field.getName().equals("user"))
      .forEach(field -> {
        field.setAccessible(Boolean.TRUE);
        try {
          Object newValue = field.get(source);
          Object oldValue = field.get(target);

          if (newValue instanceof String newStr && oldValue instanceof String oldStr) {
            if (!newStr.trim().equalsIgnoreCase(oldStr)) {
              field.set(target, newStr);
            }
          }
          else if (Objects.nonNull(newValue) && !newValue.equals(oldValue)) {
            field.set(target, newValue);
          }
        }
        catch (IllegalAccessException e) {
          throw new ApplicationException(
            HttpStatus.NOT_MODIFIED,
            "Cannot make update. Failed to update field: %s".formatted(field.getName())
          );
        }
      });
  }
}
