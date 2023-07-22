package subway.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import subway.domain.Section;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SectionResponse {

    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private Long distance;

    public static SectionResponse from(Section section) {
        return SectionResponse.builder()
            .id(section.getId())
            .lineId(section.getLine().getId())
            .upStationId(section.getUpStation().getId())
            .downStationId(section.getDownStation().getId())
            .distance(section.getDistance())
            .build();
    }

}
