package nextstep.subway.section.dto;

import lombok.Value;

@Value
public class CreateSectionRequest {
    Long upStationId;
    Long downStationId;
    Long distance;
}
