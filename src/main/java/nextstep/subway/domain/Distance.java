package nextstep.subway.domain;

import nextstep.subway.common.ErrorMessage;

import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    private int distance;

    public Distance(int distance) {
        this.distance = distance;
    }


    protected Distance() {

    }

    public int getDistance() {
        return distance;
    }

    public void decrease(int value) {
        if (distance - value <= 0) {
            throw new IllegalStateException(ErrorMessage.INVALID_DISTANCE.toString());
        }
        distance = distance - value;
    }
}
