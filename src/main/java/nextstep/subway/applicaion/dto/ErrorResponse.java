package nextstep.subway.applicaion.dto;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class ErrorResponse{

    private HttpStatus status;

    private String reason;

    @CreatedDate
    private LocalDateTime date;

    public ErrorResponse(HttpStatus status, String reason, LocalDateTime date) {
        this.status = status;
        this.reason = reason;
        this.date = date;
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
