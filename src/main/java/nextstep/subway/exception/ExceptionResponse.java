package nextstep.subway.exception;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ExceptionResponse {
    private String message;

    public ExceptionResponse() {
    }

    public ExceptionResponse(String message) {
        this.message = message;
    }
}
