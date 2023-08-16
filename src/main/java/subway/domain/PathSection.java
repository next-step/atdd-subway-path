package subway.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(of = {"id", "upStation", "downStation"})
public class PathSection {

    @Getter
    private final PathSection.Id id;
    @Getter
    private PathStation upStation;
    @Getter
    private PathStation downStation;
    @Getter
    private Kilometer distance;

    public static PathSection of(PathSection.Id id, PathStation startStation, PathStation endStation, Kilometer kilometer) {
        return new PathSection(id, startStation, endStation, kilometer);
    }

    private PathSection(PathSection.Id id, PathStation upStation, PathStation downStation, Kilometer distance) {
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public double getDistanceToDouble() {
        return distance.getValue().doubleValue();
    }


    @EqualsAndHashCode(of = {"id"})
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Id {
        private Long id;

        private Id(Long id) {
            this.id = id;
        }
        public static PathSection.Id of(Long id) {
            return new PathSection.Id(id);
        }
        public static PathSection.Id register() {
            return new PathSection.Id();
        }

        public Long getValue() {
            return id;
        }

        public boolean isNew() {
            return id == null;
        }

    }
}
