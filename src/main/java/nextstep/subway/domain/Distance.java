package nextstep.subway.domain;

import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    private int distance;
    private static final int MIN_DISTANCE = 0;

    protected Distance() {
    }

    public Distance(int distance) {
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }

    public boolean islessThanMinDistance() {
        return this.distance <= MIN_DISTANCE;
    }

    public Distance minus(int minusDistance) {
        this.distance -= minusDistance;

        return this;
    }

    public Distance add(int addDistance) {
        this.distance += addDistance;

        return this;
    }
}
