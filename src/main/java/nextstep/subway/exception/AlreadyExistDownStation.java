package nextstep.subway.exception;

public class AlreadyExistDownStation extends BusinessException{

    private static final String ERROR_MESSAGE = "하행역이 이미 등록되어 있습니다.";

    public AlreadyExistDownStation() {
        super(ERROR_MESSAGE);
    }
}
