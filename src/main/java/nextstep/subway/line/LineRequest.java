package nextstep.subway.line;

import nextstep.subway.section.Section;

public class LineRequest {
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private Long distance;

    public LineRequest(String name, String color, Long upStationId, Long downStationId,
        Long distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Line toEntity() {
        Line line = new Line(name, color);
        Section section = new Section(getUpStationId(), getDownStationId(), getDistance());
        line.addSection(section);

        return line;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public void setUpStationId(Long upStationId) {
        this.upStationId = upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public void setDownStationId(Long downStationId) {
        this.downStationId = downStationId;
    }

    public Long getDistance() {
        return distance;
    }

    public void setDistance(Long distance) {
        this.distance = distance;
    }
}
