package nextstep.subway.exception;

import lombok.Getter;

import java.util.NoSuchElementException;

@Getter
public class NotExistElementException extends NoSuchElementException {

    private String message;


    public NotExistElementException(String message) {
        this.message = message;
    }

}
