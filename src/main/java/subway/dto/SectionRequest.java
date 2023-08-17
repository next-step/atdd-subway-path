package subway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;

@Builder
@Getter
@AllArgsConstructor
public class SectionRequest {

    private Long upStationId;
    private Long downStationId;
    private Long distance;

    public Section toSection(Line line, Station upStation, Station downStation) {
        return Section.builder()
            .line(line)
            .upStation(upStation)
            .downStation(downStation)
            .distance(this.distance)
            .build();
    }

}
