package nextstep.subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateCreationException extends RuntimeException {
    public DuplicateCreationException() {
        super(Messages.DUPLICATE_CREATION.message());
    }
}
