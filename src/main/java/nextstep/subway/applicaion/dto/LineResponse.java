package nextstep.subway.applicaion.dto;

import lombok.Getter;
import nextstep.subway.domain.Line;

import java.util.Collections;
import java.util.List;

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

    public static LineResponse of(final Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(), getStationResponses(line));
    }

    public static List<StationResponse> getStationResponses(final Line line) {
        if (line.hasEmptySection()) {
            return Collections.emptyList();
        }

        return StationResponse.of(line.getStations());
    }
}

