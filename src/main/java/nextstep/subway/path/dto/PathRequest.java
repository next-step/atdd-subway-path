package nextstep.subway.path.dto;

import nextstep.subway.path.domain.PathType;

public class PathRequest {
    private Long source;
    private Long target;
    private PathType type;

    public PathRequest() {
    }

    public PathRequest(final Long source, final Long target, final PathType type) {
        this.source = source;
        this.target = target;
        this.type = type;
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

    public PathType getType() {
        return type;
    }

    public void setType(final PathType type) {
        this.type = type;
    }
}
