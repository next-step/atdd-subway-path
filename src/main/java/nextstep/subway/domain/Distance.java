package nextstep.subway.domain;

import nextstep.subway.exception.InvalidSectionDistanceException;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Distance {

    public static final int MIN_VALUE = 1;

    protected Distance() {
    }

    private int value;

    public Distance(int value) {
        if (value < MIN_VALUE) {
            throw new InvalidSectionDistanceException(value);
        }
        this.value = value;
    }

    public int value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Distance distance = (Distance) o;
        return value == distance.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
