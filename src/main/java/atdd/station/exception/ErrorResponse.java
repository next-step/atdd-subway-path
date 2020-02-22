package atdd.station.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorResponse {
    private long code;
    private String message;
    @JsonIgnore
    private HttpStatus status;

    @Builder
    private ErrorResponse(long code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
