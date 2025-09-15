package krilovs.andrejs.app.loan.application;

import krilovs.andrejs.app.profile.ProfileMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
  componentModel = "spring",
  uses = {ProfileMapper.class}
)
public interface LoanApplicationMapper {
  @Mapping(target = "customer", ignore = true)
  @Mapping(target = "createdAt", defaultExpression = "java(java.time.LocalDateTime.now())")
  @Mapping(
    target = "status",
    defaultExpression = "java(krilovs.andrejs.app.loan.application.LoanApplicationStatus.NEW)"
  )
  LoanApplicationEntity toEntity(LoanApplicationDto dto);

  @Mapping(target = "profile", source = "customer")
  @Mapping(target = "customerId", source = "customer.id")
  LoanApplicationDto toDto(LoanApplicationEntity entity);
}