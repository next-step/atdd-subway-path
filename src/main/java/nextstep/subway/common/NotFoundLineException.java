package nextstep.subway.common;

public class NotFoundLineException extends NotFoundException {

    public NotFoundLineException(Long id) {
        super(String.format("not found line : %d", id));
    }
}
