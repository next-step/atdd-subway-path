package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Station;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class PathResponse {

    private List<StationInfo> stations;
    private int distance;

    public PathResponse(List<Station> stationList, int distance) {
        this.stations = stationList.stream()
                .map(StationInfo::of)
                .collect(Collectors.toList());
        this.distance = distance;
    }

    public static PathResponse of(List<Station> stations, int distance){
        return new PathResponse(stations, distance);
    }

    public List<StationInfo> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }

    static class StationInfo {
        private Long id;
        private String name;
        private LocalDateTime createdDate;

        public StationInfo(Long id, String name, LocalDateTime createdDate) {
            this.id = id;
            this.name = name;
            this.createdDate = createdDate;
        }

        public static StationInfo of(Station station){
            return new StationInfo(
                    station.getId(),
                    station.getName(),
                    station.getCreatedDate()
            );
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public LocalDateTime getCreatedDate() {
            return createdDate;
        }
    }

}
