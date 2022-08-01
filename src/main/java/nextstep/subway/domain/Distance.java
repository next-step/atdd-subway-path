package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.enums.SubwayErrorMessage;

import javax.persistence.Embeddable;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Distance {
    private int value;

    public static Distance of(int distance) {
        return new Distance(distance);
    }

    public Distance getAddedDistanceBy(Distance newDistance) {
        return of(this.value + newDistance.getValue());
    }

    public Distance getDecreaseDistanceBy(Distance newDistance) {
        checkToDecreaseDistance(newDistance);
        return of(this.value - newDistance.getValue());
    }

    private void checkToDecreaseDistance(Distance newDistance) {
        if (!isOriginalDistanceLongThenNew(newDistance)) {
            throw new IllegalArgumentException(SubwayErrorMessage.INVALID_DISTANCE.getMessage());
        }
    }

    private boolean isOriginalDistanceLongThenNew(Distance newDistance) {
        return newDistance.getValue() < this.value;
    }

}
