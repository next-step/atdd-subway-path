package nextstep.subway.domain;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import nextstep.subway.domain.exception.DistanceSizeException;

@Embeddable
public class Distance {

    private static final int MIN = 1;

    @Column(name = "distance")
    private int value;

    protected Distance() {
    }

    public Distance(final int value) {
        if (value < MIN) {
            throw new DistanceSizeException();
        }
        this.value = value;
    }

    public Distance minus(final Distance minusDistance) {
        if (this.value - minusDistance.value < MIN) {
            throw new DistanceSizeException();
        }
        this.value -= minusDistance.value;
        return this;
    }

    public Distance plus(final Distance plusDistance) {
        this.value += plusDistance.value;
        return this;
    }

    public boolean more(final Distance distance) {
        return this.value >= distance.value;
    }

    public boolean isUnderMin() {
        return this.value < MIN;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Distance distance = (Distance) o;
        return value == distance.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
