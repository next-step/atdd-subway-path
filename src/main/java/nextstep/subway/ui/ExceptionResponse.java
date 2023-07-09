package nextstep.subway.ui;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ExceptionResponse {
    private String message;

    public ExceptionResponse(final String message) {
        this.message = message;
    }
}
