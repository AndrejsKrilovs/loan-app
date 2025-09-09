package krilovs.andrejs.app.user.active;

import krilovs.andrejs.app.user.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ActiveUserMapper {
  @Mapping(target = "id", source = "user.id")
  @Mapping(target = "password", ignore = true)
  @Mapping(target = "role", source = "user.role")
  @Mapping(target = "email", source = "user.email")
  @Mapping(target = "enabled", source = "user.enabled")
  UserDto toUserDto(ActiveUserEntity entity);
}
