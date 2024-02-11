package nextstep.subway.path;

import nextstep.subway.station.Station;

public interface PathFinder {
    Path shortcut(Station upStation,
                  Station downStation);
}
