package nextstep.subway.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
public final class SubwayLineRequest {
    private final String name;
    private final String color;
    private final Long distance;
    private final Long upStationId;
    private final Long downStationId;

    @Builder
    public SubwayLineRequest(String name, String color, Long distance, Long upStationId, Long downStationId) {
        this.name = name;
        this.color = color;
        this.distance = distance;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
    }

    public static class SubwayLineRequestBuilder {
        public SubwayLineRequestBuilder() {
        }
    }
}
