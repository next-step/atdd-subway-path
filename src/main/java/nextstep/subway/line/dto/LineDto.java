package nextstep.subway.line.dto;

import lombok.Builder;
import lombok.Getter;
import nextstep.subway.exception.ErrorCode;
import nextstep.subway.exception.SubwayException;
import nextstep.subway.line.entity.Line;
import nextstep.subway.section.entity.Section;
import nextstep.subway.station.dto.StationDto;
import nextstep.subway.station.entity.Station;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public static LineDto of(String name, String color, Long upStationId, Long downStationId, Integer distance) {
        return LineDto.builder()
                .name(name)
                .color(color)
                .upStationId(upStationId)
                .downStationId(downStationId)
                .distance(distance)
                .build();
    }

    public static LineDto of(Line subwayLine) {
        return LineDto.builder()
                .id(subwayLine.getId())
                .name(subwayLine.getName())
                .color(subwayLine.getColor())
                .upStationId(subwayLine.getUpStation().getId())
                .downStationId(subwayLine.getDownStation().getId())
                .distance(subwayLine.getDistance())
                .stationDtos(Stream.of(
                                StationDto.from(subwayLine.getUpStation()),
                                StationDto.from(subwayLine.getDownStation())
                            ).collect(Collectors.toSet()))
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

    public static List<LineDto> toLineDtos(List<Line> lines) {
        List<LineDto> lineDtos = new ArrayList<>();
        // 반복문 실행 -> 만약 1호선, 2호선등 노선이 여러개일때 생성하기 위함.
        for (Line line : lines) {
            Set<StationDto> stations = new LinkedHashSet<>();

            // 첫번째 구간 찾기
            Section firstSection = getFirstSection(line);
            stations.add(StationDto.from(firstSection.getUpStation()));

            // 첫번째 구간 외 나머지 구간 찾기
            Section nextSection = firstSection;
            while (nextSection != null) {
                stations.add(StationDto.from(nextSection.getDownStation()));
                nextSection = getNextSection(nextSection, line);
            }
            lineDtos.add(LineDto.builder()
                    .id(line.getId())
                    .name(line.getName())
                    .color(line.getColor())
                    .upStationId(line.getUpStation().getId())
                    .downStationId(line.getDownStation().getId())
                    .distance(line.getDistance())
                    .stationDtos(stations)
                    .build());
        }
        return lineDtos;
    }

    private static Section getFirstSection(Line line) {
        List<Station> downStations = line.getSections().stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());

        return line.getSections().stream()
                .filter(it -> !downStations.contains(it.getUpStation()))
                .findFirst()
                .orElseThrow(() -> new SubwayException(ErrorCode.INVALID_UP_STATION));
    }

    private static Section getNextSection(Section section, Line line) {
        return line.getSections().stream()
                .filter(it -> it.getUpStation().equals(section.getDownStation()))
                .findFirst()
                .orElse(null);
    }
}
