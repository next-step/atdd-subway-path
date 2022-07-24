package nextstep.subway.applicaion.dto;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;

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

    public static LineResponse convertedBy(Line line) {
        return new LineResponse(
            line.getId(),
            line.getName(),
            line.getColor(),
            createStationResponses(line)
        );
    }

    private static List<StationResponse> createStationResponses(Line line) {
        if (line.getLineSection().isEmpty()) {
            return Collections.emptyList();
        }

        return line.getLineSection().getStations().stream()
            .map(StationResponse::convertedBy)
            .collect(Collectors.toList());
    }
}

