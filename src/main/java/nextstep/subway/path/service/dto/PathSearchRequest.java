package nextstep.subway.path.service.dto;

public class PathSearchRequest {

    private Long source;
    private Long target;

    public PathSearchRequest() {
    }

    public PathSearchRequest(final Long source, final Long target) {
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
