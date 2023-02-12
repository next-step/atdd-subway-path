package nextstep.subway.applicaion;

public class LineNotFoundException extends RuntimeException {
    public final static String MESSAGE = "해당 지하철노선을 찾을 수 없습니다. (요청 값: %d)";
    public LineNotFoundException(long lineId) {
        super(String.format(MESSAGE, lineId));
    }
}
