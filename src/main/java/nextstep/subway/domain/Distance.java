package nextstep.subway.domain;

import nextstep.subway.ui.exception.AddSectionException;

import javax.persistence.Embeddable;

@Embeddable
public class Distance {
    private int distance;

    protected Distance() { }

    public Distance(int distance) {
        this.distance = distance;
    }

    public Distance calculate(Section newSection) {
        validateDistance(newSection);
        return new Distance(distance - newSection.getDistance());
    }

    private void validateDistance(Section newSection) {
        if (this.distance <= newSection.getDistance()) {
            throw new AddSectionException(
                    String.format("새로 추가되는 구간 거리는 기존 구간의 거리 이상일 수 없습니다. 기존 구간 거리 = %d, 신규 구간 거리 = %d",
                            this.distance, newSection.getDistance()));
        }
    }

    public int getDistance() {
        return distance;
    }
}
