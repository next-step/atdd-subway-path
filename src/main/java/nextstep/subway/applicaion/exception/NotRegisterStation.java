package nextstep.subway.applicaion.exception;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class NotRegisterStation extends RuntimeException{
    private final HttpStatus status = HttpStatus.BAD_REQUEST;
    private final String reason;

    @CreatedDate
    private LocalDateTime date;

    public NotRegisterStation(String message) {
        super(message);
        this.reason = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }

    public LocalDateTime getDate() {
        return date;
    }
}
