package nextstep.subway.line.domain;

import nextstep.subway.station.entity.Station;

import java.math.BigInteger;
import java.util.List;

public interface PathFinder {
    List<Station> getPath();

    BigInteger getWeight();
}
