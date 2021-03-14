package nextstep.subway.line.exception;

public class LineNotFoundException extends RuntimeException {

    public LineNotFoundException() {
        super("지하철 노선이 존재하지 않습니다.");
    }
}
