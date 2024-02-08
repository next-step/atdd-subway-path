package nextstep.subway.domain.station;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nextstep.subway.domain.station.entity.Station;

public class StationInfo {

    @Getter
    @RequiredArgsConstructor
    public static class Main {
        private final Long id;
        private final String name;

        public static Main from(Station entity) {
            return new Main(entity.getId(), entity.getName());
        }
    }
}
