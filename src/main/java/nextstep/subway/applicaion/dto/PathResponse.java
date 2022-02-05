package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Station;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class PathResponse {

    private List<StationResponse> stations;
    private int distance;

    public static PathResponse of(List<Station> stations, int distance) {
        return new PathResponse(stations.stream().map(StationResponse::of).collect(Collectors.toList()), distance);
    }

    private PathResponse(List<StationResponse> stationResponses, int distance) {
        this.stations = stationResponses;
        this.distance = distance;
    }

    public static class StationResponse {
        private Long id;
        private String name;
        private LocalDateTime createdAt;

        private static StationResponse of(Station station) {
            return new StationResponse(
                    station.getId(),
                    station.getName(),
                    station.getCreatedDate()
            );
        }

        private StationResponse(Long id, String name, LocalDateTime createdAt) {
            this.id = id;
            this.name = name;
            this.createdAt = createdAt;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }
}
