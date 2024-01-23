package subway.section.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SectionControllerAdvice {

    private final Logger log = LoggerFactory.getLogger(SectionControllerAdvice.class);

    @ExceptionHandler(SectionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String sectionException(SectionException e) {
        log.debug("SectionException 발생 ::: {}", e.getMessage());
        return e.getMessage();
    }
}
