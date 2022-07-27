package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {

    @Column(name = "distance")
    public int value;

    protected Distance(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
