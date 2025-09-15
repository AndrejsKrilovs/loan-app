package krilovs.andrejs.app.loan.application;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import krilovs.andrejs.app.exception.ApplicationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class LoanApplicationSpecificationTest {
  @Mock
  private Root<LoanApplicationEntity> root;

  @Mock
  private CriteriaQuery<?> query;

  @Mock
  private CriteriaBuilder cb;

  @Mock
  private Predicate basePredicate;

  @Mock
  private Path<Object> path;

  @BeforeEach
  void setUp() {
    Mockito.when(cb.conjunction()).thenReturn(basePredicate);
  }

  @Test
  void testBuildSelectSpecificationWithStatus() {
    var params = Map.of("status", "APPROVED");
    var statusPredicate = mockPredicate(root.get("status"), LoanApplicationStatus.APPROVED);

    var spec = LoanApplicationSpecification.buildSelectSpecification(params);
    var result = spec.toPredicate(root, query, cb);

    Assertions.assertEquals(statusPredicate, result);
    Mockito.verify(cb).equal(root.get("status"), LoanApplicationStatus.APPROVED);
    Mockito.verify(cb).and(basePredicate, statusPredicate);
  }

  @Test
  void testBuildSelectSpecificationWithCustomerId() {
    var params = Map.of("customerId", "123");
    var customerPath = mockPath(root, "customer");
    var customerIdPath = mockPath(customerPath, "id");
    var customerPredicate = mockPredicate(customerIdPath, 123L);

    var spec = LoanApplicationSpecification.buildSelectSpecification(params);
    var result = spec.toPredicate(root, query, cb);

    Assertions.assertEquals(customerPredicate, result);
    Mockito.verify(query).orderBy(cb.desc(root.get("createdAt")));
  }

  @Test
  void testBuildSelectSpecificationWithCreatedRange() {
    var params = Map.of(
      "createdFrom", "2025-01-01",
      "createdTo", "2025-12-31"
    );

    var createdAtPath = mockPath(root, "createdAt");
    var betweenPredicate = mockBetweenPredicate(
      (Path<LocalDate>) createdAtPath,
      LocalDate.parse(params.get("createdFrom")),
      LocalDate.parse(params.get("createdTo"))
    );

    var spec = LoanApplicationSpecification.buildSelectSpecification(params);
    var result = spec.toPredicate(root, query, cb);
    Assertions.assertEquals(betweenPredicate, result);
  }

  @Test
  void testBuildSelectSpecificationWithInvalidParams() {
    var spec = LoanApplicationSpecification.buildSelectSpecification(Map.of());
    Assertions.assertThrows(ApplicationException.class, () -> spec.toPredicate(root, query, cb));
  }

  @Test
  void testBuildSelectSpecificationWithPartialCreatedRange() {
    var params = Map.of("createdFrom", "2025-01-01");
    var spec = LoanApplicationSpecification.buildSelectSpecification(params);

    Assertions.assertThrows(ApplicationException.class, () -> spec.toPredicate(root, query, cb));
  }

  private Path<?> mockPath(Path<?> parent, String attribute) {
    Mockito.when(parent.get(attribute)).thenReturn(path);
    return path;
  }

  private <T> Predicate mockPredicate(Path<?> path, T value) {
    Mockito.when(cb.equal(path, value)).thenReturn(basePredicate);
    Mockito.when(cb.and(basePredicate, basePredicate)).thenReturn(basePredicate);
    return basePredicate;
  }

  private Predicate mockBetweenPredicate(Path<LocalDate> path, LocalDate from, LocalDate to) {
    Mockito.when(cb.between(path, from, to)).thenReturn(basePredicate);
    Mockito.when(cb.and(basePredicate, basePredicate)).thenReturn(basePredicate);
    return basePredicate;
  }
}
