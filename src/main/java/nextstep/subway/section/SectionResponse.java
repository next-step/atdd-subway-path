package nextstep.subway.section;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SectionResponse {
    private Long upstationId;
    private String upstation;
    private Long downstationId;
    private String downstation;
    private int distance;

    public static SectionResponse from(Section section) {
        return SectionResponse.builder()
                .upstationId(section.getUpstation().getId())
                .upstation(section.getUpstation().getName())
                .downstationId(section.getDownstation().getId())
                .downstation(section.getDownstation().getName())
                .distance(section.getDistance())
                .build();
    }
}
