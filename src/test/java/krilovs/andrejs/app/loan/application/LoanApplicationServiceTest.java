package krilovs.andrejs.app.loan.application;

import krilovs.andrejs.app.exception.ApplicationException;
import krilovs.andrejs.app.profile.ProfileEntity;
import krilovs.andrejs.app.profile.ProfileRepository;
import krilovs.andrejs.app.risk.RiskAssessmentEntity;
import krilovs.andrejs.app.risk.RiskAssessmentRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class LoanApplicationServiceTest {
  @Mock
  private ProfileRepository profileRepository;

  @Mock
  private RiskAssessmentRepository riskAssessmentRepository;

  @Mock
  private LoanApplicationRepository loanApplicationRepository;

  @Mock
  private LoanApplicationMapper loanApplicationMapper;

  @InjectMocks
  private LoanApplicationService loanApplicationService;

  private ProfileEntity profile;
  private LoanApplicationDto dto;
  private SimpleLoanApplicationDto simpleDto;
  private LoanApplicationEntity entity;

  @BeforeEach
  void setUp() {
    dto = LoanApplicationDto.builder()
      .customerId(1L)
      .amount(BigDecimal.valueOf(1000))
      .termDays(Short.valueOf("30"))
      .percent(BigDecimal.valueOf(0.1))
      .status(LoanApplicationStatus.NEW)
      .build();

    simpleDto = SimpleLoanApplicationDto.builder()
      .id(1L)
      .customerId(1L)
      .status(LoanApplicationStatus.NEW)
      .build();

    profile = new ProfileEntity();
    profile.setId(1L);
    profile.setFirstName("Andrejs");
    profile.setLastName("Krilovs");
    profile.setBirthDate(LocalDate.of(2000, 1, 1));

    entity = new LoanApplicationEntity();
    entity.setId(1L);
    entity.setCustomer(profile);
    entity.setStatus(LoanApplicationStatus.NEW);
  }

  @Test
  void shouldReturnLoanApplications() {
    Mockito.when(loanApplicationRepository.findAll(ArgumentMatchers.<Specification<LoanApplicationEntity>>any()))
      .thenReturn(List.of(entity));
    Mockito.when(loanApplicationMapper.toDto(entity)).thenReturn(dto);

    var result = loanApplicationService.getLoanApplications(Map.of("status", "NEW"));
    Assertions.assertFalse(result.isEmpty());
  }

  @Test
  void shouldThrowWhenNoLoanApplicationsFound() {
    Mockito.when(loanApplicationRepository.findAll(ArgumentMatchers.<Specification<LoanApplicationEntity>>any()))
      .thenReturn(List.of());

    ApplicationException ex = Assertions.assertThrows(
      ApplicationException.class,
      () -> loanApplicationService.getLoanApplications(Map.of())
    );

    Assertions.assertEquals(HttpStatus.OK, ex.getStatus());
    Assertions.assertEquals("No loans at this moment", ex.getMessage());
  }

  @Test
  void shouldCreateLoanApplication() {
    Mockito.when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));
    Mockito.when(loanApplicationMapper.toEntity(dto)).thenReturn(entity);
    Mockito.when(
      loanApplicationRepository.add(
        entity.getAmount(),
        entity.getPercent(),
        entity.getTermDays(),
        entity.getStatus().name(),
        entity.getCustomer().getId()
      )
    ).thenReturn(Optional.of(entity));
    Mockito.when(loanApplicationMapper.toDto(entity)).thenReturn(dto);

    var result = loanApplicationService.createLoanApplication(dto);
    Assertions.assertEquals(dto, result);
  }

  @Test
  void shouldThrowExceptionWhenProfileNotFound() {
    Mockito.when(profileRepository.findById(1L)).thenReturn(Optional.empty());

    ApplicationException ex = Assertions.assertThrows(
      ApplicationException.class,
      () -> loanApplicationService.createLoanApplication(dto)
    );

    Assertions.assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    Assertions.assertEquals("Profile not found", ex.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenProfileAlreadyHaveActiveLoanApplication() {
    Mockito.when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));
    Mockito.when(loanApplicationMapper.toEntity(dto)).thenReturn(entity);
    Mockito.when(
      loanApplicationRepository.add(
        entity.getAmount(),
        entity.getPercent(),
        entity.getTermDays(),
        entity.getStatus().name(),
        entity.getCustomer().getId()
      )
    ).thenReturn(Optional.empty());

    ApplicationException ex = Assertions.assertThrows(
      ApplicationException.class,
      () -> loanApplicationService.createLoanApplication(dto)
    );

    Assertions.assertEquals(HttpStatus.CONFLICT, ex.getStatus());
    Assertions.assertTrue(ex.getMessage().contains("User already have opened loan in status 'NEW' or 'UNDER_REVIEW'"));
  }
  @Test
  void shouldThrowExceptionWhenLoanApplicationNotFound() {
    Mockito.when(loanApplicationRepository.update("APPROVED", 1L)).thenReturn(Optional.empty());
    ApplicationException ex = Assertions.assertThrows(
      ApplicationException.class,
      () -> loanApplicationService.changeLoanApplicationStatus(LoanApplicationStatus.APPROVED, 1L)
    );

    Assertions.assertEquals(HttpStatus.NOT_ACCEPTABLE, ex.getStatus());
    Assertions.assertTrue(ex.getMessage().contains("Status 'APPROVED' cannot be changed for non existing loan application"));
  }

  @Test
  void shouldThrowExceptionWhenProfileIsYoungerThan18() {
    profile.setBirthDate(LocalDate.now().minusYears(17L));
    Mockito.when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));

    var ex = Assertions.assertThrows(
      ApplicationException.class,
      () -> loanApplicationService.createLoanApplication(dto)
    );

    Assertions.assertEquals(HttpStatus.TOO_EARLY, ex.getStatus());
    Assertions.assertEquals(
      "To create loan application, requested person must be older than 18 years old.",
      ex.getMessage()
    );
    Mockito.verify(loanApplicationRepository, Mockito.never()).save(Mockito.any());
  }

  @Test
  void shouldChangeLoanApplicationStatusToUnderReview() {
    entity.setStatus(LoanApplicationStatus.UNDER_REVIEW);

    Mockito.when(loanApplicationRepository.update("UNDER_REVIEW", 1L))
      .thenReturn(Optional.of(entity));
    Mockito.when(loanApplicationMapper.toSimpleDto(entity)).thenReturn(simpleDto);

    var result = loanApplicationService.changeLoanApplicationStatus(LoanApplicationStatus.UNDER_REVIEW, 1L);
    Assertions.assertEquals(simpleDto, result);
    Mockito.verify(riskAssessmentRepository).save(Mockito.any(RiskAssessmentEntity.class));
  }

  @Test
  void shouldThrowExceptionWhenLoanApplicationAlreadyInReview() {
    entity.setStatus(LoanApplicationStatus.UNDER_REVIEW);

    Mockito.when(loanApplicationRepository.update("UNDER_REVIEW", 1L))
      .thenReturn(Optional.of(entity));
    Mockito.doThrow(DataIntegrityViolationException.class)
      .when(riskAssessmentRepository).save(Mockito.any(RiskAssessmentEntity.class));

    var ex = Assertions.assertThrows(
      ApplicationException.class,
      () -> loanApplicationService.changeLoanApplicationStatus(LoanApplicationStatus.UNDER_REVIEW, 1L)
    );

    Assertions.assertEquals(HttpStatus.CONFLICT, ex.getStatus());
    Assertions.assertTrue(ex.getMessage().contains("already in status 'UNDER_REVIEW'"));
  }

  @Test
  void shouldChangeLoanApplicationStatusToReject() {
    entity.setStatus(LoanApplicationStatus.REJECTED);

    Mockito.when(loanApplicationRepository.update("REJECTED", 1L))
      .thenReturn(Optional.of(entity));
    Mockito.when(loanApplicationMapper.toSimpleDto(entity)).thenReturn(simpleDto);

    var result = loanApplicationService.changeLoanApplicationStatus(LoanApplicationStatus.REJECTED, 1L);
    Assertions.assertEquals(simpleDto, result);
    Mockito.verify(riskAssessmentRepository).deleteById(1L);
  }

  @Test
  void shouldNotChangeStatusIfOtherStatusProvided() {
    entity.setStatus(LoanApplicationStatus.EXPIRED);

    Mockito.when(loanApplicationRepository.update("EXPIRED", 1L))
      .thenReturn(Optional.of(entity));
    Mockito.when(loanApplicationMapper.toSimpleDto(entity)).thenReturn(simpleDto);

    var result = loanApplicationService.changeLoanApplicationStatus(LoanApplicationStatus.EXPIRED, 1L);
    Assertions.assertEquals(simpleDto, result);
    Mockito.verifyNoInteractions(riskAssessmentRepository);
  }
}