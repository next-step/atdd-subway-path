package nextstep.subway.applicaion.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class NotRegisterStationException extends RuntimeException {
    private final HttpStatus status = HttpStatus.BAD_REQUEST;
    private final String reason;
    private final LocalDateTime date;

    public NotRegisterStationException(String message) {
        super(message);
        this.reason = message;
        this.date = LocalDateTime.now();
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
