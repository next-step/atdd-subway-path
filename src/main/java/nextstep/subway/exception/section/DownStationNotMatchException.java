package nextstep.subway.exception.section;

public class DownStationNotMatchException extends RuntimeException {

    private static final String MESSAGE = "기존 하행역과 새로운 상행역이 일치하지 않습니다. - %s";

    public DownStationNotMatchException(String stationName) {
        super(String.format(MESSAGE, stationName));
    }

}
