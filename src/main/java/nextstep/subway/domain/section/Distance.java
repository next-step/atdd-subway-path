package nextstep.subway.domain.section;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Getter
@Embeddable
@NoArgsConstructor
public class Distance {
    private int distance;

    public Distance(int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("거리는 1 이상이어야 합니다.");
        }
        this.distance = distance;
    }
}
