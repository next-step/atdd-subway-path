package subway.dto.response;

import lombok.Getter;
import subway.domain.Line;
import subway.domain.Station;

import java.util.Collections;
import java.util.List;

@Getter
public class LineResponse {

    private Long id;

    private String color;

    private String name;

    private List<StationResponse> stations;

    public LineResponse() {
    }

    public LineResponse(Line line) {
        this.id = line.getId();
        this.color = line.getColor();
        this.name = line.getName();
    }


    public LineResponse(Line line, List<StationResponse> stationResponses) {
        this.id = line.getId();
        this.color = line.getColor();
        this.name = line.getName();
        this.stations = stationResponses;
    }

    public static LineResponse createLineResponse(Line line){
        return new LineResponse(line,createStationResponses(line.getStations()));
    }

    private static List<StationResponse> createStationResponses(List<Station> stations) {
        if (stations.isEmpty()) {
            return Collections.emptyList();
        }
        return stations.stream().map(StationResponse::new).toList();
    }

}
