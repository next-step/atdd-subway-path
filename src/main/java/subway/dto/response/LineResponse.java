package subway.dto.response;


import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import subway.entity.Line;
import subway.entity.Station;

import java.util.List;

@Getter
public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    private Long totalDistance;

    @Builder
    public LineResponse(Long id, String name, String color, List<StationResponse> stations, Long totalDistance) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
        this.totalDistance = totalDistance;
    }

    public static LineResponse from(Line line) {
        return LineResponse.builder()
            .id(line.getId())
            .name(line.getName())
            .color(line.getColor())
            .stations(line.getStations().stream()
                .map(StationResponse::from)
                .collect(Collectors.toList()))
            .totalDistance(line.getSections().totalDistance())
            .build();
    }
}
