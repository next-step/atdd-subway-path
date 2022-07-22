package nextstep.subway.applicaion.dto;

import lombok.Getter;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

import java.util.List;

@Getter
public class SectionResponse {
    private Long lineId;
    private String name;
    private Station upStation;
    private Station downStation;
    private int distance;

    public SectionResponse(Long lineId, String name, Station upStation, Station downStation, int distance) {
        this.lineId = lineId;
        this.name = name;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static SectionResponse of(Section section) {
        return new SectionResponse(
                section.getLine().getId(),
                section.getLine().getName(),
                section.getUpStation(),
                section.getDownStation(),
                section.getDistance()
        );
    }
}

