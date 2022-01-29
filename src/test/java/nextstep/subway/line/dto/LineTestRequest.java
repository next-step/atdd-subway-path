package nextstep.subway.line.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LineTestRequest {
    private String upStationName;
    private String downStationName;
    private String lineName;
    private String lineColor;
    private int distance;

    @Builder
    private LineTestRequest(String upStationName, String downStationName, String lineName,
                            String lineColor, int distance) {
        this.upStationName = upStationName;
        this.downStationName = downStationName;
        this.lineName = lineName;
        this.lineColor = lineColor;
        this.distance = distance;
    }
}
