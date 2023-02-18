package nextstep.subway.global.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
public class ErrorResponse {

    private HttpStatus status;
    private List<String> errorMessages;

    public ErrorResponse(HttpStatus status, List<String> errorMessages) {
        this.status = status;
        this.errorMessages = errorMessages;
    }

    public static ErrorResponse of(HttpStatus status, List<String> errorMessages) {
        return new ErrorResponse(status, errorMessages);
    }
}
