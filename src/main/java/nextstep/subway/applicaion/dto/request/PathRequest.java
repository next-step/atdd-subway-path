package nextstep.subway.applicaion.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PathRequest {
    @NotNull(message = "목적지는 필수 입력 값입니다." )
    private Long source ;
    @NotNull(message = "출발지는 필수 입력 값입니다.")
    private Long target;
}
