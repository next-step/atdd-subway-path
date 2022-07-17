package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class Distance {
    private final static int MIN_VALUE = 1;

    private int value;

    public Distance(int value) {
        if (value < MIN_VALUE) {
            throw new IllegalArgumentException();
        }
        this.value = value;
    }
}
