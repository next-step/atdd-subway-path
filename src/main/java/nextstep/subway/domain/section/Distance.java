package nextstep.subway.domain.section;

import nextstep.subway.exception.SubwayRestApiException;
import javax.persistence.Embeddable;
import static nextstep.subway.exception.ErrorResponseEnum.ERROR_INVAILD_DISTANCE;

@Embeddable
public class Distance {
    private int distance;
    private static final int MIN_DISTANCE = 0;

    protected Distance() {
    }

    public Distance(int distance) {
        validation(distance);

        this.distance = distance;
    }

    private void validation(int distance) {
        if (distance <= MIN_DISTANCE){
            throw new SubwayRestApiException(ERROR_INVAILD_DISTANCE);
        }
    }

    public int getDistance() {
        return distance;
    }

    public int minus(int minusDistance) {
        return this.distance -= minusDistance;
    }

    public int add(int addDistance) {
        return this.distance += addDistance;
    }
}
