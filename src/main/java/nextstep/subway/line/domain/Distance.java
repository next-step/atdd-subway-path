package nextstep.subway.line.domain;

import nextstep.subway.error.DistanceInputException;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Distance {

    @Column
    private int distance;

    public Distance() { }

    public Distance(int distance) {
        if (checkDistance(distance) == 0) {
            throw new DistanceInputException();
        }

        this.distance = distance;
    }

    private int checkDistance(int distance) {
        return distance > 0 ? 1 : 0;
    }

    public int getDistance() {
        return distance;
    }

    public int distanceDivide(int newDistance) {
        if (newDistance >= this.distance) {
            throw new RuntimeException("추가하는 구간의 거리가 잘못되었습니다.");
        }

        return this.distance - newDistance;
    }

    public int sumDistance(Section nextSection) {
        return this.distance + nextSection.getDistance().distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Distance distance1 = (Distance) o;

        return new EqualsBuilder().append(distance, distance1.distance).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(distance).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("distance", distance)
                .toString();
    }
}
