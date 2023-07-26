package nextstep.subway.line;

import nextstep.subway.station.Station;

public class PathInfo {

    private final Station upStation;
    private final Station downStation;
    private final int distance;

    public PathInfo(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }
}
