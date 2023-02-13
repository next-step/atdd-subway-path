package nextstep.subway.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorResponse {

    private HttpStatus status;

    private String errorCode;

    private String errorMessage;

    private Object data;

    public ErrorResponse(HttpStatus status, String errorCode, String errorMessage, Object data) {
        this.status = status;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.data = data;
    }
}
