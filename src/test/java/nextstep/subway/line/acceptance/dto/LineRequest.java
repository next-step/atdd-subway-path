package nextstep.subway.line.acceptance.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LineRequest {
    private String upStationName;
    private String downStationName;
    private String lineName;
    private String lineColor;
    private int distance;

    @Builder
    private LineRequest(String upStationName, String downStationName, String lineName,
                        String lineColor, int distance) {
        this.upStationName = upStationName;
        this.downStationName = downStationName;
        this.lineName = lineName;
        this.lineColor = lineColor;
        this.distance = distance;
    }
}
