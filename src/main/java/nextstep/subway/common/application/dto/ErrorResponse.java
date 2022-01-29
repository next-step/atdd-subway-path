package nextstep.subway.common.application.dto;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private final String message;
    private final LocalDateTime date;

    public ErrorResponse(String message) {
        this.message = message;
        this.date = LocalDateTime.now();
    }
}
