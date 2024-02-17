package nextstep.subway.domain.line.dto.request;

import lombok.Getter;

@Getter
public class CreateSectionRequest {

    private Long downStationId;
    private Long upStationId;
    private int distance;
}
