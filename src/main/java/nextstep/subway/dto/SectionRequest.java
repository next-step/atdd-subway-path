package nextstep.subway.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
public class SectionRequest {
    private final Long upStationId;
    private final Long downStationId;
    private final Long distance;

    @Builder(access = AccessLevel.PUBLIC)
    public SectionRequest(Long upStationId, Long downStationId, Long distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }


    public static class SectionRequestBuilder {
        public SectionRequestBuilder() {
        }
    }
}
