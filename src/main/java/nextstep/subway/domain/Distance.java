package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.util.HashMap;
import java.util.Map;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class Distance {
    private final static int MIN_VALUE = 1;

    private int value;

    private Distance(int value) {
        this.value = value;
    }

    public static Distance valueOf(double value) {
        return valueOf((int) value);
    }

    public static Distance valueOf(int value) {
        if (value < MIN_VALUE) {
            throw new IllegalArgumentException();
        }
        Distance distanceCache = DistanceCache.findByNumber(value);
        if (distanceCache != null) {
            return distanceCache;
        }
        return DistanceCache.cacheDistance(new Distance(value));
    }

    public Distance reduce(Distance distance) {
        return Distance.valueOf(value - distance.toInt());
    }

    public Distance increase(Distance distance) {
        return Distance.valueOf(value + distance.toInt());
    }

    public int toInt() {
        return value;
    }

    private static class DistanceCache {
        private static final Map<Integer, Distance> distanceCache = new HashMap<>();

        private static Distance cacheDistance(Distance distance) {
            distanceCache.put(distance.toInt(), distance);
            return distance;
        }

        private static Distance findByNumber(int number) {
            return distanceCache.get(number);
        }
    }
}
