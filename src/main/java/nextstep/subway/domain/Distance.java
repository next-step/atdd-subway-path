package nextstep.subway.domain;

import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Distance {
    int distance;

    public Distance(int distance) {
        this.distance = distance;
    }

    public boolean isMoreLongerThan(Distance distance) {
        return this.distance >= distance.getDistance();
    }

    public int getBetweenDistanceAbsolute(Distance distance) {
        return Math.abs(this.distance - distance.getDistance());
    }
}
