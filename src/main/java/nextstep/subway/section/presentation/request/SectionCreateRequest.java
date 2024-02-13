package nextstep.subway.section.presentation.request;

import lombok.Getter;
import nextstep.subway.section.domain.Section;

@Getter
public class SectionCreateRequest {

    private Long upStationId;
    private Long downStationId;
    private int distance;

    public SectionCreateRequest(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static Section toEntity(Long upStationId, Long downStationId, int distance) {
        return Section.builder()
                .upStationId(upStationId)
                .downStationId(downStationId)
                .distance(distance)
                .build();
    }
}