package krilovs.andrejs.app.utility;

import java.util.Arrays;
import java.util.Objects;
import lombok.experimental.UtilityClass;

@UtilityClass
public class EntityUpdater {
  /**
   * Обновляет поля target значениями из source, если они отличаются.
   * Работает для любых Entity через рефлексию.
   */
  public static <T> void updateFields(T source, T target) {
    if (Objects.isNull(source) || Objects.isNull(target)) {
      return;
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
          } else if (!Objects.equals(newValue, oldValue) && newValue != null) {
            field.set(target, newValue);
          }
        } catch (IllegalAccessException e) {
          throw new RuntimeException("Failed to update field: " + field.getName(), e);
        }
      });
  }
}
