package nextstep.subway.applicaion.dto.response;

import lombok.Getter;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

import java.util.Collections;
import java.util.List;

@Getter
public class LineResponse {

    private Long id;

    private String color;

    private String name;

    private int totalDistance;

    private List<StationResponse> stations;

    public LineResponse() {
    }

    public LineResponse(Line line) {
        this.id = line.getId();
        this.color = line.getColor();
        this.name = line.getName();
        this.totalDistance = line.getTotalDistance();
    }


    public LineResponse(Line line, List<StationResponse> stationResponses) {
        this.id = line.getId();
        this.color = line.getColor();
        this.name = line.getName();
        this.totalDistance = line.getTotalDistance();
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
