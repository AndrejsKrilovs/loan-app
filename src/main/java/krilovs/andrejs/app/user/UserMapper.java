package krilovs.andrejs.app.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
  @Mapping(target = "enabled", defaultExpression = "java(Boolean.TRUE)")
  @Mapping(target = "role", defaultExpression = "java(krilovs.andrejs.app.user.UserRole.CUSTOMER)")
  UserEntity toEntity(UserDto dto);

  @Mapping(target = "password", ignore = true)
  UserDto toDto(UserEntity entity);
}
