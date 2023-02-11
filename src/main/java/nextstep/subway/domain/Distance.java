package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {

    public static final int LOWER_LIMIT = 0;

    @Column(name = "distance")
    private int value;

    protected Distance() {}

    public Distance(int value) {
        validateDistance(value);

        this.value = value;
    }

    private static void validateDistance(int value) {
        if (value <= LOWER_LIMIT) {
            throw new IllegalArgumentException(
                    String.format("지하철 구간 길이는 %d보다 커야합니다. (요청값: %d)", LOWER_LIMIT, value)
            );
        }
    }

    public int getValue() {
        return value;
    }
}
