package nextstep.subway.common;

import lombok.Getter;

@Getter
public final class DomainException extends RuntimeException {

    private String code;

    private Object data;

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Object data) {
        super(message);
        this.data = data;
    }

    public DomainException(String message, String code) {
        super(message);
        this.code = code;
    }

    public DomainException(DomainExceptionType exceptionType) {
        super(exceptionType.getMessage());
        this.code = exceptionType.getCode();
    }

    public DomainException(DomainExceptionType exceptionType, Object data) {
        super(exceptionType.getMessage());
        this.code = exceptionType.getCode();
        this.data = data;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
