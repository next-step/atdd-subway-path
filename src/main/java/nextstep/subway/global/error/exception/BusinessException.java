package nextstep.subway.global.error.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private int status;

    public BusinessException(int status, String message) {
        super(message);
        this.status = status;
    }

    public BusinessException(String errorMessage) {
        super(errorMessage);
    }

}
