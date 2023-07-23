package nextstep.subway.applicaion.exception.domain;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class CustomException extends RuntimeException {

    private String message;

    public CustomException(String message) {
        this.message = message;
    }

    public abstract HttpStatus getStatus();
}