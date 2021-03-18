package nextstep.subway.exception;

public class InValidUpStationException extends BusinessException{

    private static final String ERROR_MESSAGE = "상행역은 하행 종점역이어야 합니다.";

    public InValidUpStationException() {
        super(ERROR_MESSAGE);
    }
}
