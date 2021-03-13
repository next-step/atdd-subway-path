package nextstep.subway.exceptions;

public class AlreadyExistsEntityException extends RuntimeException{

    public static final String DEFAULT_EXCEPTION_MSG = "이미 존재하는 엔티티입니다.";
    public static final String EXCEPTION_MSG = "%s은 이미 존재합니다.";


    public AlreadyExistsEntityException() {
        super(DEFAULT_EXCEPTION_MSG);
    }

    public AlreadyExistsEntityException(String param) {
        super(String.format(EXCEPTION_MSG, param));
    }
}
