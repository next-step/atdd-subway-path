package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Station;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class PathResponse {

    private List<StationResponse> stations;

    public List<StationResponse> getStations() {
        return stations;
    }

    public static PathResponse of(List<Station> stations) {
        PathResponse response = new PathResponse();
        response.stations = stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());

        return response;
    }

    @Override
    public String toString() {
        return "PathResponse{" +
                "stations=" + stations +
                '}';
    }

    public static class StationResponse {

        private Long id;
        private String name;
        private LocalDateTime createdAt;

        private static StationResponse of(Station station) {
            StationResponse response = new StationResponse();
            response.id = station.getId();
            response.name = station.getName();
            response.createdAt = station.getCreatedDate();

            return response;
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

        @Override
        public String toString() {
            return "StationResponse{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", createdAt=" + createdAt +
                    '}';
        }
    }

}
