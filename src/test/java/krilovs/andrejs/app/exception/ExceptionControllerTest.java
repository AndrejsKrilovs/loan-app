package krilovs.andrejs.app.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class ExceptionControllerTest {
  @InjectMocks
  ExceptionController exceptionController;

  @Mock
  HttpServletRequest request;

  @Test
  void shouldHandleApplicationException() {
    Mockito.when(request.getRequestURI()).thenReturn("/api/v1/users");

    var exception = new ApplicationException(HttpStatus.BAD_REQUEST, "Invalid request data");
    var response = exceptionController.handleApplicationException(exception, request);
    Assertions.assertNotNull(response);
    Assertions.assertEquals(400, response.getStatusCode().value());

    var body = response.getBody();
    Assertions.assertNotNull(body);
    Assertions.assertEquals(HttpStatus.BAD_REQUEST, body.status());
    Assertions.assertEquals("Invalid request data", body.message());
    Assertions.assertEquals("/api/v1/users", body.path());
    Assertions.assertNotNull(body.date());
    Assertions.assertTrue(body.date().isBefore(LocalDateTime.now().plusSeconds(1)));
  }
}