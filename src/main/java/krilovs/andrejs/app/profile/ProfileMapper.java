package krilovs.andrejs.app.profile;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
  @Mapping(target = "user.id", source = "userId")
  ProfileEntity toEntity(ProfileDto dto);

  @Mapping(target = "userId", source = "user.id")
  ProfileDto toDto(ProfileEntity entity);
}
