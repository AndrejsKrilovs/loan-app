package krilovs.andrejs.app.loan.application;

import krilovs.andrejs.app.profile.ProfileMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.Objects;

@Mapper(
  componentModel = "spring",
  uses = {ProfileMapper.class}
)
public interface LoanApplicationMapper {
  @Mapping(target = "customer", ignore = true)
  @Mapping(target = "createdAt", defaultExpression = "java(java.time.LocalDateTime.now())")
  @Mapping(target = "percent", source = "percent", qualifiedByName = "mapPercentToEntity")
  @Mapping(
    target = "status",
    defaultExpression = "java(krilovs.andrejs.app.loan.application.LoanApplicationStatus.NEW)"
  )
  LoanApplicationEntity toEntity(LoanApplicationDto dto);

  @Mapping(target = "profile", source = "customer")
  @Mapping(target = "customerId", source = "customer.id")
  @Mapping(target = "percent", source = "percent", qualifiedByName = "mapPercentVisualisationToDto")
  LoanApplicationDto toDto(LoanApplicationEntity entity);

  @Mapping(target = "customerId", source = "customer.id")
  @Mapping(target = "percent", source = "percent", qualifiedByName = "mapPercentVisualisationToDto")
  SimpleLoanApplicationDto toSimpleDto(LoanApplicationEntity entity);

  @Named("mapPercentVisualisationToDto")
  default BigDecimal mapPercentVisualisationToDto(BigDecimal percent) {
    return Objects.requireNonNullElse(percent, BigDecimal.ZERO).scaleByPowerOfTen(2);
  }

  @Named("mapPercentToEntity")
  default BigDecimal mapPercentToEntity(BigDecimal percent) {
    return Objects.requireNonNullElse(percent, BigDecimal.ZERO).scaleByPowerOfTen(-2);
  }
}