package nextstep.subway.line.exception;

public class SectionDuplicatedException extends RuntimeException {

    public SectionDuplicatedException() {
        super("지하철 구간이 중복되었습니다.");
    }
}
