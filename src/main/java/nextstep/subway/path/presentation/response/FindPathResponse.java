package nextstep.subway.path.presentation.response;

import nextstep.subway.station.service.dto.ShowStationDto;

import java.util.List;
import java.util.Objects;

public class FindPathResponse {

    private List<ShowStationDto> stations;

    private Integer distance;

    private FindPathResponse() {
    }

    private FindPathResponse(List<ShowStationDto> stations, Integer distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public static FindPathResponse of(List<ShowStationDto> stations, Integer distance) {
        return new FindPathResponse(stations, distance);
    }

    public List<ShowStationDto> getStations() {
        return stations;
    }

    public Integer getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FindPathResponse that = (FindPathResponse) o;
        return stations.equals(that.stations) && distance.equals(that.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stations, distance);
    }

}
