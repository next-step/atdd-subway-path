package nextstep.subway.line.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nextstep.subway.common.exception.BadRequestException;

import javax.persistence.Embeddable;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Distance {
    public static final String DISTANCE_EXCEPTION_MESSAGE = "기존 구간의 길이보다 작은 길이의 구간만 추가할 수 있습니다.";
    public static final int VALIDATE_DISTANCE_CRITERIA = 0;

    private int distance;

    private Distance(int distance) {
        validateDistance(distance);
        this.distance = distance;
    }

    public static Distance of(int distance) {
        return new Distance(distance);
    }

    private void validateDistance(int distance) {
        if(distance <= VALIDATE_DISTANCE_CRITERIA) {
            throw new BadRequestException(DISTANCE_EXCEPTION_MESSAGE);
        }
    }

    public int getValue() {
        return distance;
    }
}
