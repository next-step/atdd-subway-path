package nextstep.subway.domain;

import nextstep.subway.exception.InvalidSectionDistanceException;

import javax.persistence.Embeddable;

@Embeddable
public class Distance {

    private static final int MIN_VALUE = 1;

    protected Distance() {
    }

    private int value;

    public Distance(int value) {
        if (value < MIN_VALUE) {
            throw new IllegalArgumentException();
        }
        this.value = value;
    }

    public void decrease(int value) {
        if (isLessThan(value)) {
            throw new InvalidSectionDistanceException(this.value, value);
        }
        this.value -= value;
    }

    private boolean isLessThan(int value) {
        return this.value <= value;
    }

    public int value() {
        return value;
    }
}
