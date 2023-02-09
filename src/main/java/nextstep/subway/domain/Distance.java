package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Distance {

    @Column(name = "distance")
    private int value;

    public static Distance of(int value) {
        return new Distance(value);
    }

    public Distance() {
    }

    public Distance(int value) {
        assert (value > 0);
        this.value = value;
    }

    public Distance minus(Distance that) {
        return new Distance(this.value - that.value);
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
