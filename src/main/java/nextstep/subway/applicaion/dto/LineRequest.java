package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

import javax.validation.constraints.NotBlank;

public class LineRequest {
    @NotBlank(message = "노선의 이름은 필수값입니다.")
    private String name;
    @NotBlank(message = "노선의 색깔은 필수값입니다.")
    private String color;
    private Long upStationId;
    private Long downStationId;
    private Integer distance;

    private LineRequest() {}

    private LineRequest(final String name, final String color, final Long upStationId, final Long downStationId, final Integer distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static LineRequest from(final String name, final String color) {
        return new LineRequest(name, color, null, null, null);
    }

    public Line toEntity() {
        return new Line(this.name, this.color);
    }

    public Line toEntityAddStation(final Station upStation, final Station downStation) {
        return new Line(this.name, this.color, upStation, downStation, this.distance);
    }

    public boolean canAddSection() {
        return this.upStationId != null && this.downStationId != null && this.distance != 0;
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

    public int getDistance() {
        return distance;
    }
}
