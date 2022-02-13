package nextstep.subway.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Distance {

    @Column(name = "distance")
    private int value;

    protected Distance() {
    }

    private Distance(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("거리는 0이하로 설정 불가능합니다.");
        }

        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Distance from(int value) {
        return new Distance(value);
    }

    public void reduceDistance(int distance) {
        validateDivideDistance(distance);

        this.value -= distance;
    }

    private void validateDivideDistance(int distance) {
        if (canNotDivideDistance(distance)) {
            throw new IllegalArgumentException("거리는 추가할 구간 사이의 거리보다 크거나 0 이하로 설정할 수 없습니다.");
        }
    }

    private boolean canNotDivideDistance(int value) {
        return this.value <= value || value <= 0;
    }

    public void addDistance(int distance) {
        value += distance;
    }

    @Override
    public boolean equals(Object target) {
        if (this == target) {
            return true;
        }

        if (target == null || !(target instanceof Distance)) {
            return false;
        }

        Distance distance = (Distance) target;

        return value == distance.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
