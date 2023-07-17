package nextstep.subway.line.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SectionRequest {
    private Long downStationId;
    private Long upStationId;
    private Integer distance;
}
