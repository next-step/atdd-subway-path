package nextstep.subway.domain.line.dto.response;

import lombok.Builder;
import lombok.Getter;
import nextstep.subway.domain.line.domain.Section;

@Getter
public class SectionResponse {

    private Long id;
    private String lineName;
    private String downStationName;
    private String upStationName;

    @Builder
    private SectionResponse(Long id, String lineName, String downStationName, String upStationName) {
        this.id = id;
        this.lineName = lineName;
        this.downStationName = downStationName;
        this.upStationName = upStationName;
    }

    public static SectionResponse from(Section section) {
        return SectionResponse.builder()
                .id(section.getId())
                .downStationName(section.getDownStation().getName())
                .upStationName(section.getUpStation().getName())
                .lineName(section.getLine().getName())
                .build();
    }
}
