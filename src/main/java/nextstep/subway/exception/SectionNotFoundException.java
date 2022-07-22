package nextstep.subway.exception;

public class SectionNotFoundException extends RuntimeException {

    public SectionNotFoundException() {
        super("해당하는 구간을 찾을 수 없습니다.");
    }

}
