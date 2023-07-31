package nextstep.subway.line.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SaveLineSectionRequest {

    @NotNull
    private Long upStationId;

    @NotNull
    private Long downStationId;

    @NotNull
    @Min(value = 1, message = "구간 거리는 1 이상이어야 합니다.")
    private Integer distance;

}
