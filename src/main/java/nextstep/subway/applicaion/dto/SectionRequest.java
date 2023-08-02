package nextstep.subway.applicaion.dto;

import lombok.Getter;

@Getter
public class SectionRequest {
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;
}
