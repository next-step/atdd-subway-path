package nextstep.subway.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ErrorResponse {
    private String message;

    @Builder
    public ErrorResponse(String message) {
        this.message = message;
    }
}
