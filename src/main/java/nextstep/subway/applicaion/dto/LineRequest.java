package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Line;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class LineRequest {
    @NotBlank(message = "노선 이름은 필수 입니다.")
    @Pattern(regexp = "^[가-힣0-9]{2,30}$", message = "노선 이름은 2자이상 30자 미만 한글과 숫자만 입력이 가능 합니다. 공백불가")
    private String name;

    @NotBlank(message = "노선 색은 필수 입니다.")
    private String color;

    private Long upStationId;
    private Long downStationId;
    private int distance;

    public LineRequest() {
    }

    public LineRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Line toLine() {
        return new Line(name, color);
    }
    public boolean hasSectionAvailable() {
        if (upStationId == null || downStationId == null) {
            return false;
        }

        if (upStationId > 0 && downStationId > 0 && distance > 0) {
            return true;
        }
        return false;
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
