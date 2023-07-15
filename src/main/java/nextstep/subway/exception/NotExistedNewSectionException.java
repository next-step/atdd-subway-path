package nextstep.subway.exception;

public class NotExistedNewSectionException extends NewSectionException {

    private static final String MSG = "상행역과 하행역 둘 중 하나도 노선에 포함되어있지 않은 경우 등록 불가능 합니다.";

    public NotExistedNewSectionException() {
        super(MSG);
    }
}
