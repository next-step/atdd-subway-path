package nextstep.subway.exceptions;

public class OnlyOneSectionException extends RuntimeException {
    public static final String DEFAULT_MSG = "지하철 노선 구간이 하나뿐이기에 삭제가 불가능합니다.";

    public OnlyOneSectionException() {
        super(DEFAULT_MSG);
    }

    public OnlyOneSectionException(String message) {
        super(message);
    }
}
