package nextstep.subway.path.dto;

public class PathRequest {
    private Long source;
    private Long target;

    public PathRequest() {
    }

    public PathRequest(final Long source, final Long target) {
        this.source = source;
        this.target = target;
    }

    public Long getSource() {
        return source;
    }

    public void setSource(final Long source) {
        this.source = source;
    }

    public Long getTarget() {
        return target;
    }

    public void setTarget(final Long target) {
        this.target = target;
    }
}
