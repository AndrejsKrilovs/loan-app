package krilovs.andrejs.app.profile;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
  @Mapping(
    target = "id",
    ignore = true
  )
  @Mapping(
    target = "user",
    ignore = true
  )
  @Mapping(
    target = "version",
    ignore = true
  )
  ProfileEntity toEntity(ProfileDto dto);

  @Mapping(target = "userId", source = "user.id")
  ProfileDto toDto(ProfileEntity entity);
}
