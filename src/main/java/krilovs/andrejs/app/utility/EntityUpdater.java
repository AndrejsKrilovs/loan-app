package krilovs.andrejs.app.utility;

import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.Metamodel;
import krilovs.andrejs.app.exception.ApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.beans.PropertyDescriptor;
import java.util.Objects;

@Slf4j
@Component
public class EntityUpdater {
  private final Metamodel metamodel;

  public EntityUpdater(EntityManager em) {
    this.metamodel = em.getMetamodel();
  }

  public <T> void updateFields(T source,
                               T target,
                               Class<T> entityClass) {
    if (Objects.isNull(source) || Objects.isNull(target)) {
      return;
    }

    var entityType = metamodel.entity(entityClass);
    for (var attr : entityType.getAttributes()) {
      String name = attr.getName();

      if ("user".equals(name)) {
        continue;
      }

      try {
        var propertyDescriptor = new PropertyDescriptor(
          name,
          source.getClass()
        );
        var getter = propertyDescriptor.getReadMethod();
        var setter = propertyDescriptor.getWriteMethod();

        if (Objects.isNull(getter) || Objects.isNull(setter)) {
          continue;
        }

        var newValue = getter.invoke(source);
        var oldValue = getter.invoke(target);

        if (newValue instanceof String newStr && oldValue instanceof String oldStr) {
          if (!oldStr.equalsIgnoreCase(newStr.trim())) {
            setter.invoke(
              target,
              newStr
            );
          }
        }
        else if (Objects.nonNull(newValue) && !newValue.equals(oldValue)) {
          setter.invoke(
            target,
            newValue
          );
        }
      }
      catch (Exception e) {
        throw new ApplicationException(
          HttpStatus.EXPECTATION_FAILED,
          "Failed to update field %s".formatted(name)
        );
      }
    }
  }
}
