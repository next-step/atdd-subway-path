package nextstep.subway.acceptance.utils;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SubwayLineInfo {
    Long upStationId;
    Long downStationId;
    Long subwayLineId;
    Long middleStationId;
    Long distance;

    public static class SubwayLineInfoBuilder {
        public SubwayLineInfoBuilder() {
        }
    }
}
