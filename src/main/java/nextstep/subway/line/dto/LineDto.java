package nextstep.subway.line.dto;

import lombok.Builder;
import lombok.Getter;
import nextstep.subway.line.entity.Line;
import nextstep.subway.section.entity.Section;
import nextstep.subway.station.dto.StationDto;
import nextstep.subway.station.entity.Station;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class LineDto {

    private final Long id;
    private final String name;
    private final String color;
    private final Long upStationId;
    private final Long downStationId;
    private final Integer distance;
    private final Set<StationDto> stationDtos;

    @Builder
    public LineDto(Long id, String name, String color, Long upStationId, Long downStationId, Integer distance, Set<StationDto> stationDtos) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
        this.stationDtos = stationDtos;
    }

    public static LineDto from(Line subwayLine) {
        return LineDto.builder()
                .id(subwayLine.getId())
                .name(subwayLine.getName())
                .color(subwayLine.getColor())
                .upStationId(subwayLine.getUpStation().getId())
                .downStationId(subwayLine.getDownStation().getId())
                .distance(subwayLine.getSections().getDistance())
                .stationDtos(subwayLine.getStations().stream()
                        .map(StationDto::from)
                        .collect(Collectors.toCollection(LinkedHashSet::new)))
                .build();
    }

    public Line toEntity(Station upStation, Station downStation, Section section) {
        return Line.builder()
                .name(name)
                .color(color)
                .upStation(upStation)
                .downStation(downStation)
                .distance(distance)
                .section(section)
                .build();
    }
}
