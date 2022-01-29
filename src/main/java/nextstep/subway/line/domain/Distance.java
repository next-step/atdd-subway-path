package nextstep.subway.line.domain;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Getter;
import nextstep.subway.common.domain.model.exception.ErrorMessage;

@Getter
@Embeddable
public class Distance {
    @Column(name = "DISTANCE", nullable = false)
    private int value;

    protected Distance() {
    }

    public Distance(int value) {
        verifyZero(value);
        this.value = value;
    }

    private void verifyZero(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_DISTANCE.getMessage());
        }
    }

    public boolean greaterThan(Distance distance) {
        return this.value > distance.value;
    }

    public Distance addition(Distance thatDistance) {
        return new Distance(value + thatDistance.value);
    }

    public Distance subtraction(Distance thatDistance) {
        return new Distance(value - thatDistance.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Distance distance = (Distance)o;
        return value == distance.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
