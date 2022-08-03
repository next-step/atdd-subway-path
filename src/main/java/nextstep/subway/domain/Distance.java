package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {

    @Column(name = "distance")
    public int value;

    protected Distance() {

    }

    protected Distance(final int value) {
        validate(value);
        this.value = value;
    }

    public void validate(final int value) {
        if(value <= 0) {
            throw new IllegalStateException("거리는 0이거나 음수가 될 수 없습니다.");
        }
    }

    public int getValue() {
        return value;
    }

    public Distance subtract(Distance otherDistance) {
        return new Distance(this.getValue() - otherDistance.getValue());
    }

    public Distance plus(Distance otherDistance) {
        return new Distance(this.getValue() + otherDistance.getValue());
    }

}
