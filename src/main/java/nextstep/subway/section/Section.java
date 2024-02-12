package nextstep.subway.section;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import nextstep.subway.line.Line;
import nextstep.subway.station.Station;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "line_id", nullable = false)
    private Line line;

    @ManyToOne
    @JoinColumn(name = "up_station_id", nullable = false)
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "down_station_id", nullable = false)
    private Station downStation;

    @Column
    private long distance;

    public Section() {}

    public Section(Line line, Station upStation, Station downStation, long distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public boolean isExistSection(Section section) {
        return this.upStation == section.downStation;
    }

    public boolean isConnectedSection(Section section) {
        return this.downStation == section.upStation;
    }

    public long getId() {
        return id;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public void remove() {
        line.getSections().remove(this);
    }

    public boolean isMatchDownStation(Station station) {
        return this.downStation == station;
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
//                ", line=" + line +
                ", upStation=" + upStation +
                ", downStation=" + downStation +
                ", distance=" + distance +
                '}';
    }
}
