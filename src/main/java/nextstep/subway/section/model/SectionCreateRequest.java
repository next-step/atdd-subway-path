package nextstep.subway.section.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SectionCreateRequest {
    private Long downStationId;
    private Long upStationId;
    private int distance;
}
