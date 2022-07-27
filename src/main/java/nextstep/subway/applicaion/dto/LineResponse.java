package nextstep.subway.applicaion.dto;

import lombok.Getter;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Sections;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    private LineResponse(Long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse of(Line line) {
        return new LineResponse(
            line.getId(),
            line.getName(),
            line.getColor(),
            createStationResponses(line)
        );
    }

    private static List<StationResponse> createStationResponses(Line line) {
        Sections sections = line.getSections();
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        return sections.getStations().stream()
                       .map(StationResponse::of)
                       .collect(Collectors.toList());
    }
}

