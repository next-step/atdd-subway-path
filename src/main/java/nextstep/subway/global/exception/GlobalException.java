package nextstep.subway.global.exception;

import lombok.Getter;

@Getter
public class GlobalException extends RuntimeException {
    public GlobalException(String message) {
        super(message);
    }
}
