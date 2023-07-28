package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stations {

    private Set<Station> stations;
}
