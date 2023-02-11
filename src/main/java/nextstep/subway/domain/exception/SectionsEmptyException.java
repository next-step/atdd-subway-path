package nextstep.subway.domain.exception;

import javax.persistence.EntityNotFoundException;

public class SectionsEmptyException extends EntityNotFoundException {
    private static final String MESSAGE = "구간 목록이 비어있습니다.";

    public SectionsEmptyException() {
        super(MESSAGE);
    }
}
