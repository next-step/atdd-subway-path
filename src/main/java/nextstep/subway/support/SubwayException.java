package nextstep.subway.support;

import lombok.Getter;

@Getter
public abstract class SubwayException extends RuntimeException {
    private final ErrorCode errorCode;

    public SubwayException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public SubwayException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public SubwayException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public SubwayException(Throwable cause, ErrorCode errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    public SubwayException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace,
                           ErrorCode errorCode) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorCode = errorCode;
    }


}
