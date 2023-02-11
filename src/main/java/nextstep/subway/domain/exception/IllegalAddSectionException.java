package nextstep.subway.domain.exception;

public class IllegalAddSectionException extends RuntimeException{

    public IllegalAddSectionException() {
        super("역을 추가 할 수 없습니다");
    }

}
