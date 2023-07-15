package nextstep.subway.exception;

public class DuplicationNewSectionException extends NewSectionException {

    private static final String MSG = "상행역과 하행역이 이미 노선에 모두 등록되어 있는 경우 등록 불가능 합니다.";

    public DuplicationNewSectionException() {
        super(MSG);
    }
}