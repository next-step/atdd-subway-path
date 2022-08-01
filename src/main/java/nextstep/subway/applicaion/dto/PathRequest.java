package nextstep.subway.applicaion.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.exception.PathException;

@Getter
@NoArgsConstructor
public class PathRequest {

    private Long source;
    private Long target;

    public PathRequest(Long source, Long target) {
        validateEquals(source, target);
        this.source = source;
        this.target = target;
    }

    private void validateEquals(Long source, Long target) {
        if (source.equals(target)) {
            throw new PathException("동일한 역은 입력할 수 없습니다.");
        }
    }

}
