package nextstep.subway.applicaion.dto;

import java.util.List;
import lombok.Getter;

@Getter
public class LineResponse {

    private final Long id;
    private final String name;
    private final String color;
    private final int totalDistance;
    private final List<StationResponse> stations;

    public LineResponse(Long id, String name, String color, LineStationsDto stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations.getStations();
        this.totalDistance = stations.getTotalDistance();
    }
}

