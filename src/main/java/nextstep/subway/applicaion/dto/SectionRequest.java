package nextstep.subway.applicaion.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SectionRequest {
    private Long upStationId;
    private Long downStationId;
    private int distance;
}
