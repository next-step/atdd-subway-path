package nextstep.subway;

import lombok.Getter;

@Getter
public class FailResponse {
    private String message;

    public FailResponse(String message) {
        this.message = message;
    }
}
