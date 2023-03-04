package nextstep.subway.applicaion.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LineRequest {

    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;
}
