package nextstep.subway.global.error.exception;

import lombok.Getter;

@Getter
public class UserException extends RuntimeException {
    private int status;

    public UserException(int status, String message) {
        super(message);
        this.status = status;
    }
}
