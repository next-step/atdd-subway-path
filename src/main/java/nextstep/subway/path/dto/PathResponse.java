package nextstep.subway.path.dto;

import nextstep.subway.station.dto.StationResponse;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

public class PathResponse {

    private List<StationResponse> stations;

    private int distance;

    public PathResponse(List<StationResponse> stations, int distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }

    public void setStations(List<StationResponse> stations) {
        this.stations = stations;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PathResponse that = (PathResponse) o;

        return new EqualsBuilder().append(distance, that.distance).append(stations, that.stations).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(stations).append(distance).toHashCode();
    }
}
