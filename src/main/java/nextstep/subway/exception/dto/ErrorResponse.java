package nextstep.subway.exception.dto;

import java.time.LocalDateTime;

public class ErrorResponse {
    public String errorMassage;
    public LocalDateTime timestamp;

    public ErrorResponse(String errorMassage) {
        this.errorMassage = errorMassage;
        this.timestamp = LocalDateTime.now();
    }
}
