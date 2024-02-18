package nextstep.subway.line;

public class LineNotFoundException extends RuntimeException {
    public LineNotFoundException(Long id) {
        super("존재하지 않는 노선 : " + id);
    }
}
