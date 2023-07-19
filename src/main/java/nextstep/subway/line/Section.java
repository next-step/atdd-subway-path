package nextstep.subway.line;

import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import nextstep.subway.station.Station;

@Embeddable
public class Section {


    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    private int distance;

    public Section() {
    }

    public Section(Station upStation, Station downStation, int distance) {
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


    public boolean containsStation(Station station) {
        return this.downStation.equals(station) || this.upStation.equals(station);
    }

    public boolean isSameDownStation(Station downStation) {
        return this.downStation.equals(downStation);
    }

    public boolean canLink(Section section) {
        return section.isSameAsUpStation(downStation);
    }

    private boolean isSameAsUpStation(Station station) {
        return this.upStation.equals(station);
    }

    public boolean isSameUpStation(Station upStation) {
        return this.upStation.equals(upStation);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Section)) {
            return false;
        }
        Section section = (Section) o;
        return getDistance() == section.getDistance() && Objects.equals(getUpStation(), section.getUpStation())
                && Objects.equals(getDownStation(), section.getDownStation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUpStation(), getDownStation(), getDistance());
    }
}
