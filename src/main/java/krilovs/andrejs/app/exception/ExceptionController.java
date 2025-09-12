package krilovs.andrejs.app.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionController {
  @ExceptionHandler(ApplicationException.class)
  public ResponseEntity<ExceptionResponse> handleApplicationException(
    ApplicationException exception, HttpServletRequest request
  ) {
    var result = new ExceptionResponse(
      exception.getStatus(),
      LocalDateTime.now(),
      request.getRequestURI(),
      exception.getMessage()
    );
    return ResponseEntity
      .status(result.status())
      .body(result);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ExceptionResponse> handleValidationException(
    MethodArgumentNotValidException exception, HttpServletRequest request
  ) {
    var errors = exception
      .getBindingResult()
      .getAllErrors()
      .stream()
      .collect(Collectors.groupingBy(
        error -> ((FieldError) error).getField(),
        Collectors.mapping(
          DefaultMessageSourceResolvable::getDefaultMessage,
          Collectors.toSet()
        )
      ));

    var result = new ExceptionResponse(
      HttpStatus.valueOf(exception.getBody()
        .getStatus()),
      LocalDateTime.now(),
      request.getRequestURI(),
      errors
    );
    return ResponseEntity
      .status(result.status())
      .body(result);
  }
}
