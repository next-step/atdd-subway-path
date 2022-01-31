package nextstep.subway.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Line {

    private String name;
    private String color;
    private String upStationId;
    private String downStationId;
}
