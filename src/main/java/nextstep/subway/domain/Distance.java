package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {

    private static final int MIN_VALUE = 1;

    protected Distance() {
    }

    @Column(name = "distance")
    private int value;

    public Distance(int value) {
        if (value < MIN_VALUE) {
            throw new IllegalArgumentException();
        }
        this.value = value;
    }

    public int value() {
        return value;
    }
}
