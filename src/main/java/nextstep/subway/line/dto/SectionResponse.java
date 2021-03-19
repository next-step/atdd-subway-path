package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Sections;

import java.util.List;
import java.util.stream.Collectors;

public class SectionResponse {

    private Long id;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    private SectionResponse() {
    }

    private SectionResponse(Long id, Long upStationId, Long downStationId, int distance) {
        this.id = id;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static List<SectionResponse> of(Sections sections) {
        return sections.getSections()
                .stream()
                .map(it -> new SectionResponse(it.getId(), it.getUpStation().getId(), it.getDownStation().getId(), it.getDistance()))
                .collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }
}
