package nextstep.subway.domain.path;

import nextstep.subway.domain.Station;

public class DepartureDestinationCannotSameException extends RuntimeException {

    public final static String MESSAGE = "출발역과 도착역이 같을 수 없습니다. (요청 출발역: %d, 요청 도착역: %d)";

    public DepartureDestinationCannotSameException(Station departureStation, Station destinationStation) {
        super(String.format(MESSAGE, departureStation.getId(), destinationStation.getId()));
    }
}
