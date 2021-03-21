package nextstep.subway.exception;

public class ExistUpAndDownStationException extends BusinessException{

    private static final String ERROR_MESSAGE = "이미 존재하는 상행선과 하행선입니다.";

    public ExistUpAndDownStationException() {
        super(ERROR_MESSAGE);
    }
}

