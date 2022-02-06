package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    private static final int MIN_VALUE = 0;
    private static final String INVALID_DISTANCE_MESSAGE = "신규 역 사이의 길이가 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없습니다.";

    @Column
    private int distance;

    public Distance() {

    }

    public Distance(int value) {
        if (value <= MIN_VALUE) {
            throw new IllegalArgumentException(INVALID_DISTANCE_MESSAGE);
        }
        this.distance = value;
    }

    public Distance plus(Distance otherDistance) {
        return new Distance(distance + otherDistance.distance);
    }

    public Distance minus(Distance otherDistance) {
        return new Distance(distance - otherDistance.distance);
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Distance distance1 = (Distance) o;

        return distance == distance1.distance;
    }

    @Override
    public int hashCode() {
        return distance;
    }
}
