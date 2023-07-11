package nextstep.subway.applicaion;

import java.util.Objects;

import lombok.Getter;
import nextstep.subway.domain.station.Station;

@Getter
public class StationNode {
    private final Station station;

    public StationNode(final Station station) {
        this.station = station;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final StationNode that = (StationNode) o;
        return Objects.equals(station.getId(), that.station.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(station);
    }
}
