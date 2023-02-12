package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

public class SectionResponse {

    private Long id;
    private StationResponse upStation;
    private StationResponse downStation;
    private int distance;

    private SectionResponse(Long id, Station upStation, Station downStation, int distance) {
        this.id = id;
        this.upStation = StationResponse.of(upStation);
        this.downStation = StationResponse.of(downStation);
        this.distance = distance;
    }

    public static SectionResponse of(Section section) {
        return new SectionResponse(
                section.getId(),
                section.getUpStation(),
                section.getDownStation(),
                section.getDistance()
        );
    }

    public static List<SectionResponse> asList(Sections sections) {
        return sections.getValuesOrderBy().stream()
                .map(SectionResponse::of)
                .collect(Collectors.toUnmodifiableList());
    }

    public Long getId() {
        return id;
    }

    public StationResponse getUpStation() {
        return upStation;
    }

    public StationResponse getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }
}
