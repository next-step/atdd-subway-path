package subway.application.query.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import subway.domain.Station;
import subway.domain.SubwayLine;

import java.util.List;

public class SubwayLineResponse {
    private Long id;
    private String name;
    private String color;

    private List<StationInfo> stations;

    public SubwayLineResponse(SubwayLine.Id id, String name, String color, List<StationInfo> stations) {
        this.id = id.getValue();
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public SubwayLineResponse(Long id, String name, String color, List<StationInfo> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    private SubwayLineResponse() {
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

    public List<StationInfo> getStations() {
        return stations;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class StationInfo {
        private Long id;
        private String name;

        public StationInfo(Station.Id id, String name) {
            this.id = id.getValue();
            this.name = name;
        }

        public StationInfo(Long id, String name) {
            this.id = id;
            this.name = name;
        }

    }
}
