package nextstep.subway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class IllegalDeletionException extends RuntimeException {
    public IllegalDeletionException() {
        super(Messages.ILLEGAL_DELETION.message());
    }
}
