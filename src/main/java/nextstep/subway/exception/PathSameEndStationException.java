package nextstep.subway.exception;

public class PathSameEndStationException extends BadRequestException{
    public PathSameEndStationException() {
        super("When looking up a route, the origin and destination stations must be different.");
    }
}
