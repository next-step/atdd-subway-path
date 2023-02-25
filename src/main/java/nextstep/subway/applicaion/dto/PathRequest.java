package nextstep.subway.applicaion.dto;

public class PathRequest {
    private Long source;
    private Long target;

    public PathRequest() {
    }

    public PathRequest(Long source, Long target) {
        this.source = source;
        this.target = target;
    }

    public void setSource(Long source) {
        this.source = source;
    }

    public void setTarget(Long target) {
        this.target = target;
    }

    public Long getSource() {
        return source;
    }

    public Long getTarget() {
        return target;
    }

    @Override
    public String toString() {
        return "PathRequest{" +
            "source=" + source +
            ", target=" + target +
            '}';
    }
}
