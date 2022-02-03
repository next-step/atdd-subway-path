package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Station;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PathResponse {

    private List<PathStation> stations = new ArrayList<>();
    private int distance;

    private PathResponse(final List<PathStation> stations, final int distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public PathResponse(final List<Station> stations, final double distance) {
        this(toPathStations(stations), (int) distance);
    }

    private static List<PathStation> toPathStations(final List<Station> stations) {
        return stations.stream()
                .map(PathStation::new)
                .collect(Collectors.toList());
    }

    public static class PathStation {
        private Long id;
        private String name;

        public PathStation(final Long id, final String name) {
            this.id = id;
            this.name = name;
        }

        public PathStation(final Station station) {
            this(station.getId(), station.getName());
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    public List<PathStation> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }
}
