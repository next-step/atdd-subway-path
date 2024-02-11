package nextstep.subway.path;

public class PathRequest {
    private Long source;
    private Long target;

    public PathRequest(Long source,
                       Long target) {
        this.source = source;
        this.target = target;
    }

    public Long getSource() {
        return source;
    }

    public Long getTarget() {
        return target;
    }
}
