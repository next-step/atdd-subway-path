package nextstep.subway.applicaion.dto;

import javax.validation.constraints.NotNull;

public class PathRequest {
    @NotNull
    private Long source;
    @NotNull
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
}
