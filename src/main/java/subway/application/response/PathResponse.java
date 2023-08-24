package subway.application.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import subway.domain.Kilometer;
import subway.domain.PathStation;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PathResponse {
    private List<PathResponse.Station> stations;
    private double distance;

    private PathResponse(List<PathResponse.Station> stations, double distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public static PathResponse of(List<PathResponse.Station> stations, Kilometer distance){
        return new PathResponse(stations, distance.getValue().doubleValue());
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Station{
        private Long id;
        private String name;

        private Station(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public static Station of(PathStation.Id id, String name){
            return new Station(id.getValue(), name);
        }
    }
}
