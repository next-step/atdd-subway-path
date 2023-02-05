package nextstep.subway.domain;

import nextstep.subway.common.exception.DistanceGreaterThanException;

import javax.persistence.Embeddable;
import java.util.Objects;

import static nextstep.subway.common.error.SubwayError.NO_REGISTER_DISTANCE_GREATER_THAN;

@Embeddable
public class Distance {

    private Integer distance;

    protected Distance() {}

    public Distance(final Integer distance) {
        this.distance = distance;
    }

    public void minus(final Integer distance) {
        this.distance = this.distance - distance;
    }

    public void change(final Integer distance) {
        this.distance = distance;
    }

    public void validateGreaterThan(final Integer distance) {
        if (this.distance <= distance) {
            throw new DistanceGreaterThanException(NO_REGISTER_DISTANCE_GREATER_THAN.getMessage());
        }
    }

    public Integer getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Distance distance1 = (Distance) o;
        return Objects.equals(distance, distance1.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(distance);
    }

}
