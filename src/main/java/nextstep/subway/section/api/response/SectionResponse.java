package nextstep.subway.section.api.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.section.domain.Section;

@NoArgsConstructor
@Getter
public class SectionResponse {

    private Long sectionId;
    private Long downStationId;
    private Long upStationId;
    private int distance;

    @Builder
    public SectionResponse(Long sectionId, Long downStationId, Long upStationId, int distance) {
        this.sectionId = sectionId;
        this.downStationId = downStationId;
        this.upStationId = upStationId;
        this.distance = distance;
    }

    public static SectionResponse of(Section section) {
        return SectionResponse.builder()
                .sectionId(section.getId())
                .downStationId(section.getDownStationId())
                .upStationId(section.getUpStationId())
                .distance(section.getDistance())
                .build();
    }
}
