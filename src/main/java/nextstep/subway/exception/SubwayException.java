package nextstep.subway.exception;

import lombok.Getter;

@Getter
public class SubwayException extends RuntimeException {
    private final ErrorCode errorCode;

    public SubwayException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
