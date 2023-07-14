package subway.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorResponse {
    private String errorCode;

    private String status;

    private String message;

    @JsonIgnore
    private HttpStatus httpStatus;

    @Builder
    public ErrorResponse(String errorCode, String status, String message, HttpStatus httpStatus) {
        this.errorCode = errorCode;
        this.status = status;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
