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

    public boolean canNotDivideDistance(int value) {
        return this.value <= value;
    }

    public void validateDivideDistance(Distance value) {
        if (canNotDivideDistance(value.getValue())) {
            throw new IllegalArgumentException("추가할 구간의 사이의 거리를 초과합니다.");
        }
    }

    public void reduceDistance(Distance target) {
        validateDivideDistance(target);

        this.value -= target.getValue();
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
