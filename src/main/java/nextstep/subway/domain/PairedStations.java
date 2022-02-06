package nextstep.subway.domain;

public class PairedStations {
    private Station upStation;
    private Station downStation;

    public PairedStations(Station upStation, Station downStation) {
        if (isEqualStations(upStation, downStation)) {
            throw new IllegalArgumentException("하행역, 상행역은 동일한 역이 될 수 없습니다.");
        }

        this.upStation = upStation;
        this.downStation = downStation;
    }

    private boolean isEqualStations(Station upStation, Station downStation) {
        return upStation.equals(downStation);
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }
}
