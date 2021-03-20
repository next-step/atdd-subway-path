package nextstep.subway.line.exception;

public class GraphNullException extends RuntimeException {
    private static final String NO_GRAPH = "그래프 정보가 null 입니다.";

    public GraphNullException() {
        super(NO_GRAPH);
    }
}
