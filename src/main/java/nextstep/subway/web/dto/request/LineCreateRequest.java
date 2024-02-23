package nextstep.subway.web.dto.request;


import lombok.Getter;

@Getter
public class LineCreateRequest {

    private final String name;

    private final String color;

    private final Long upStationId;

    private final Long downStationId;

    private final Long distance;

    public LineCreateRequest(String name, String color, Long upStationId, Long downStationId, Long distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }
}
