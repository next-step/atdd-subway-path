package nextstep.subway.applicaion.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PathRequest {
    private final Long source;
    private final Long target;

    public PathRequest(Long source, Long target) {
        this.source = source;
        this.target = target;
    }
}
