package nextstep.subway.line.exception;

public class SectionNotConnectedException extends RuntimeException {

    public SectionNotConnectedException() {
        super("지하철 구간이 연결될 수 없습니다.");
    }
}
