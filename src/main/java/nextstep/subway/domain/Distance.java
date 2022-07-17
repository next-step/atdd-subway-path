package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    private final static int MIN_DISTANCE = 1;
    @Column
    private int distance;

    protected Distance() {
    }

    public Distance(int distance) {
        if (distance < MIN_DISTANCE) {
            throw new IllegalArgumentException(String.format("지하철 거리는 %d 이상이여야 합니다 ", MIN_DISTANCE));
        }
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }
}
