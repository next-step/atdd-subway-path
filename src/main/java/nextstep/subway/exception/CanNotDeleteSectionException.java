package nextstep.subway.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CanNotDeleteSectionException extends IllegalArgumentException {
    public CanNotDeleteSectionException(String message) {
        super(message);
    }
}
