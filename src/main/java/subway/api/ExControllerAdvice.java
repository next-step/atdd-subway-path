package subway.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice(annotations = RestController.class)
public class ExControllerAdvice {


  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler
  public ResponseEntity<String> exHandler(RuntimeException e) {
    System.out.println(e.getMessage());
    return ResponseEntity.badRequest().body(e.getMessage());
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler
  public ResponseEntity<String> exHandler(Exception e) {
    System.out.println(e.getMessage());
    return ResponseEntity.internalServerError().body(e.getMessage());
  }

}
