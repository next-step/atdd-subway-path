package nextstep.subway.path.domain;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.Stations;


public interface PathStrategy {

    Stations getPath(Station source, Station target);
}
