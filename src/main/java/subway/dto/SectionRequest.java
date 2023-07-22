package subway.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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
