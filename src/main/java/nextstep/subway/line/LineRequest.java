package nextstep.subway.line;

import nextstep.subway.section.Section;

public class LineRequest {
    private String name;
    private String color;
    private String upStationId;
    private String downStationId;
    private String distance;

    public Line toEntity() {
        Line line = new Line(name, color);
        Section section = new Section(getUpStationId(), getDownStationId(), getDistance());
        line.addSection(section);

        return line;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Long getUpStationId() {
        return Long.valueOf(upStationId);
    }

    public Long getDownStationId() {
        return Long.valueOf(downStationId);
    }

    public Long getDistance() {
        return Long.valueOf(distance);
    }
}
