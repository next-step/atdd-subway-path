package nextstep.subway.path.dto.request;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
public class PathRequest {

    @NotNull(message = "출발역은 반드시 입력해야 합니다.")
    private Long source;

    @NotNull(message = "도착역은 반드시 입력해야 합니다.")
    private Long target;

    public boolean isSameSourceAndTarget() {
        return source.equals(target);
    }

}
