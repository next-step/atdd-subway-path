package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

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

    public Distance minus(Distance distance) {
        if ((this.distance == distance.distance) || (distance.distance >= this.distance)) {
            throw new IllegalArgumentException("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없습니다.");
        }
        return new Distance(this.distance - distance.distance);
    }

    public Distance plus(Distance distance) {
        return new Distance(this.distance + distance.distance);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Distance distance1 = (Distance) o;
        return distance == distance1.distance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(distance);
    }


}
