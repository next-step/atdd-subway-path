package nextstep.subway.dto;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.Sections;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SectionResponse {
    private Long upStationId;
    private String upStationName;
    private Long downStationId;
    private String downStationName;
    private int distance;

    public SectionResponse(Long upStationId, String upStationName, Long downStationId, String downStationName, int distance) {
        this.upStationId = upStationId;
        this.upStationName = upStationName;
        this.downStationId = downStationId;
        this.downStationName = downStationName;
        this.distance = distance;
    }

    public static List<SectionResponse> createSectionResponse(Sections sections) {
        return sections.getSections().stream().map(
            section -> new SectionResponse(section.getUpStation().getId(),
                                           section.getUpStation().getName(),
                                           section.getDownStation().getId(),
                                           section.getDownStation().getName(),
                                           section.getDistance())
        ).collect(Collectors.toList());
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public String getUpStationName() {
        return upStationName;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public String getDownStationName() {
        return downStationName;
    }

    public int getDistance() {
        return distance;
    }
}
