package nextstep.subway.line.exception;

public class NotValidUpStationException extends RuntimeException {

    public NotValidUpStationException() {
        super("상행역은 하행 종점역이어야 합니다.");
    }
}
