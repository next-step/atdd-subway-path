package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Station;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ShortestPathResponse {

    private List<ShortestPathStationResponse> stations;

    private int distance;

    public ShortestPathResponse(final List<ShortestPathStationResponse> stations, final int distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public static ShortestPathResponse of(final List<Station> entities, final int distance) {
        return new ShortestPathResponse(entities.stream()
                .map(ShortestPathStationResponse::new)
                .collect(Collectors.toList()), distance);
    }

    public List<ShortestPathStationResponse> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }

    private static class ShortestPathStationResponse {
        private Long id;
        private String name;
        private LocalDateTime createdAt;

        public ShortestPathStationResponse(final Station station) {
            this(station.getId(), station.getName(), station.getCreatedDate());
        }

        public ShortestPathStationResponse(final Long id, final String name, final LocalDateTime createdAt) {
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
}
