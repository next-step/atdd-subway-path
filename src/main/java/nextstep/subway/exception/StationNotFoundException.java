package nextstep.subway.exception;

public class StationNotFoundException extends NotFoundException{
    public StationNotFoundException(Long id) {
        super("Station not found: " + id);
    }
}
