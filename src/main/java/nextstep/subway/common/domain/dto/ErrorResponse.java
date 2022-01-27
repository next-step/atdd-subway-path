package nextstep.subway.common.domain.dto;

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
