package nextstep.subway.line.presentation.request;

import lombok.Getter;
import nextstep.subway.line.domain.Line;

@Getter
public class LineCreateRequest {

    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public LineCreateRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static Line toEntity(LineCreateRequest request) {
        return Line.builder()
                .name(request.getName())
                .color(request.getColor())
                .build();
    }
}