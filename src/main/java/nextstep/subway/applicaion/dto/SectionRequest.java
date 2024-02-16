package nextstep.subway.applicaion.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SectionRequest {
    private Long upStationId;
    private Long downStationId;
    private int distance;
}
