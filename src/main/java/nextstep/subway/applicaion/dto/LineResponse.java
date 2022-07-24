package nextstep.subway.applicaion.dto;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    public LineResponse(Long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse from(Line line){
        return new LineResponse(
            line.getId(),
            line.getName(),
            line.getColor(),
            stationsFrom(line)
        );
    }

    public static List<StationResponse> stationsFrom(Line line) {
        if (line.isSectionEmpty()) {
            return Collections.emptyList();
        }

        List<Station> stations = line.getStations();

        return stations.stream()
            .map(StationResponse::from)
            .collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<StationResponse> getStations() {
        return stations;
    }
}

