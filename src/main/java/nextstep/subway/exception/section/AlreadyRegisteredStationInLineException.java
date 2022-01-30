package nextstep.subway.exception.section;

public class AlreadyRegisteredStationInLineException extends RuntimeException {

    private static final String MESSAGE = "새로운 구간의 하행역은 현재 등록되어있는 역일 수 없습니다. - %s";

    public AlreadyRegisteredStationInLineException(String lineName) {
        super(String.format(MESSAGE, lineName));
    }

}
