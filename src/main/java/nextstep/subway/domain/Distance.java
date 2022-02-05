package nextstep.subway.domain;

import nextstep.subway.ui.exception.SectionException;

import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    private int distance;

    protected Distance() { }

    public Distance(int distance) {
        this.distance = distance;
    }

    public Distance subtract(int newDistance) {
        validateBetWeenAddDistance(newDistance);
        return new Distance(distance - newDistance);
    }

    public Distance sum(int newDistance) {
        return new Distance(distance + newDistance);
    }

    private void validateBetWeenAddDistance(int newDistance) {
        if (this.distance <= newDistance) {
            throw new SectionException(
                    String.format("새로 추가되는 구간 거리는 기존 구간의 거리 이상일 수 없습니다. 기존 구간 거리 = %d, 신규 구간 거리 = %d",
                            this.distance, newDistance));
        }
    }

    public int getDistance() {
        return distance;
    }
}
