package nextstep.subway.common;

public class NotFoundStationException extends NotFoundException {

    public NotFoundStationException(Long id) {
        super(String.format("not found station : %d", id));
    }
}
