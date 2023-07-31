package subway.api;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import subway.core.ValidationErrorException;

import java.util.List;
import java.util.stream.Collectors;


@RestControllerAdvice(annotations = RestController.class)
public class ExControllerAdvice {


  @ExceptionHandler
  public ResponseEntity<List<ParameterValid>> exHandler(ValidationErrorException e) {
    List<ParameterValid> response = e.getErrors().stream().map(error -> new ParameterValid(error.getField(), error.getMessage())).collect(Collectors.toList());
    return ResponseEntity.badRequest().body(response);
  }

  @ExceptionHandler
  public ResponseEntity<String> exHandler(RuntimeException e) {
    return ResponseEntity.badRequest().body(e.getMessage());
  }

  @ExceptionHandler
  public ResponseEntity<String> exHandler(Exception e) {
    return ResponseEntity.internalServerError().body(e.getMessage());
  }

  @Getter
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class ParameterValid {
    private String field;
    private String message;

    public ParameterValid(String field, String message) {
      this.field = field;
      this.message = message;
    }
  }

}
