package nextstep.subway.applicaion.exception;


import org.springframework.http.HttpStatus;

public abstract class CustomException extends RuntimeException {

    private String message;

    public CustomException(String message) {
        this.message = message;
    }

    public abstract HttpStatus getStatus();
}