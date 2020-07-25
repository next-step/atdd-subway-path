package nextstep.subway.map.dto;

import nextstep.subway.map.domain.ShortestPathEnum;

public class PathRequest {
    Long source;
    Long target;
    ShortestPathEnum type;

    public PathRequest() {
    }

    public PathRequest(Long source, Long target, ShortestPathEnum type) {
        this.source = source;
        this.target = target;
        this.type = type;
    }

    public Long getSource() {
        return source;
    }

    public Long getTarget() {
        return target;
    }

    public void setSource(Long source) {
        this.source = source;
    }

    public void setTarget(Long target) {
        this.target = target;
    }

    public ShortestPathEnum getType() {
        return type;
    }

    public void setType(ShortestPathEnum type) {
        this.type = type;
    }
}
