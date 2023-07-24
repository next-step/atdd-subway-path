package nextstep.subway.line.dto;

import lombok.Builder;
import lombok.Getter;
import nextstep.subway.station.dto.StationResponse;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class LineResponse {
    private final Long id;
    private final String name;
    private final String color;
    private final Integer distance;
    private final Set<StationResponse> stations;

    @Builder
    public LineResponse(Long id, String name, String color, Integer distance, Set<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.distance = distance;
        this.stations = stations;
    }

    public static LineResponse from(LineDto dto) {
        return LineResponse.builder()
                .id(dto.getId())
                .name(dto.getName())
                .color(dto.getColor())
                .distance(dto.getDistance())
                .stations(dto.getStationDtos().stream()
                            .map(StationResponse::from)
                            .collect(Collectors.toCollection(LinkedHashSet::new)))
                .build();
    }
}
