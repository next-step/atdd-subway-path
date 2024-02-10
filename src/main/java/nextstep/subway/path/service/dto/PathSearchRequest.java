package nextstep.subway.path.service.dto;

import nextstep.subway.path.exception.PathSearchNotValidException;

import java.util.Objects;

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

    public void validate() {
        if(Objects.isNull(source)) {
            throw new PathSearchNotValidException("source can not be null");
        }

        if(Objects.isNull(target)) {
            throw new PathSearchNotValidException("target can not be null");
        }

        if (Objects.equals(source, target)) {
            throw new PathSearchNotValidException("target can not be the same with source");
        }
    }
}
