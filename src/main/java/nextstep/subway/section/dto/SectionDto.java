package nextstep.subway.section.dto;

import lombok.Builder;
import lombok.Getter;
import nextstep.subway.line.entity.Line;
import nextstep.subway.section.entity.Section;
import nextstep.subway.station.entity.Station;

@Getter
public class SectionDto {

    private Long id;
    private Long upStationId;
    private Long downStationId;
    private Integer distance;

    @Builder
    public SectionDto(Long id, Long upStationId, Long downStationId, Integer distance) {
        this.id = id;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section toEntity(Line Line, Station upStation, Station downStation) {
        return Section.builder()
                .line(Line)
                .upStation(upStation)
                .downStation(downStation)
                .distance(distance)
                .build();
    }
}
