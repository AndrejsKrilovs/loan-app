package krilovs.andrejs.app.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {
  @ExceptionHandler(ApplicationException.class)
  public ResponseEntity<ExceptionResponse> handleValidationException(ApplicationException exception,
                                                                     HttpServletRequest request) {
    var result = new ExceptionResponse(
      exception.getStatus(),
      LocalDateTime.now(),
      request.getRequestURI(),
      exception.getMessage()
    );
    return ResponseEntity
      .status(exception.getStatus())
      .body(result);
  }
}
