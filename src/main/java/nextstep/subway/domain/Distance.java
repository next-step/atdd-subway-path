package nextstep.subway.domain;

import java.util.Objects;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {

    private int value;

    protected Distance() {
    }

    public Distance(final int value) {
        this.value = value;
    }

    public Distance minus(final Distance minusDistance) {
        if (this.value <= minusDistance.value) {
            throw new IllegalArgumentException("거리는 음수가 될 수 없습니다.");
        }
        this.value -= minusDistance.value;
        return this;
    }

    public Distance plus(final Distance plusDistance) {
        this.value += plusDistance.value;
        return this;
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
