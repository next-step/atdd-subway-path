package nextstep.subway.exception;

public class StationNotExistException extends BusinessException{

    private static final String ERROR_MESSAGE = "지하철이 존재하지 않습니다";

    public StationNotExistException() {
        super(ERROR_MESSAGE);
    }
}
