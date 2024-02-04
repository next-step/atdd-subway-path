package nextstep.subway.line.controller.dto;

/** 지하철 노선 생성 요청 데이터 */
public class LineCreateRequest {

    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private Integer distance;

    public LineCreateRequest() {}

    public LineCreateRequest(
        final String name,
        final String color,
        final Long upStationId,
        final Long downStationId,
        final Integer distance
    ) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Integer getDistance() {
        return distance;
    }
}
