package nextstep.subway.line;

import com.fasterxml.jackson.annotation.JsonProperty;
import nextstep.subway.station.StationResponse;

import java.util.List;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    public LineResponse(@JsonProperty("id")Long id,
                        @JsonProperty("name") String name,
                        @JsonProperty("color") String color,
                        @JsonProperty("stations") List<StationResponse> stationResponses) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stationResponses;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return this.color;
    }

    public List<StationResponse> getStations() {
        return stations;
    }

}
