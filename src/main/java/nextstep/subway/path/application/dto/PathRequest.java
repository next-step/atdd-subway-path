package nextstep.subway.path.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PathRequest {
    private long source;
    private long target;

    private PathRequest() {
    }

    @Builder
    public PathRequest(long source, long target) {
        this.source = source;
        this.target = target;
    }
}
