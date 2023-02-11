package nextstep.subway.domain.exception;

public class NotExistBasedOnDownStationException extends IllegalArgumentException{
    private static final String MESSAGE = "역을 하행선으로 하는 구간이 존재하지 않습니다.";

    public NotExistBasedOnDownStationException() {
        super(MESSAGE);
    }
}
