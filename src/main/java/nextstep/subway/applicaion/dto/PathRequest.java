package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.exception.StationNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Objects;

public class PathRequest {
    private Long source;
    private Long target;

    public PathRequest(Long source, Long target) {
        validatePathRequest(source, target);

        this.source = source;
        this.target = target;
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

    private static void validatePathRequest(Long source, Long target) {
        if (Objects.isNull(source) || Objects.isNull(target)) {
            throw new StationNotFoundException();
        }

        if (source == target) {
            throw new DataIntegrityViolationException("출발역과 도착역이 같습니다.");
        }
    }
}
