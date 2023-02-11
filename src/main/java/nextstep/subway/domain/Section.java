package nextstep.subway.domain;

import java.util.Objects;

import javax.persistence.*;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    private int distance;

    protected Section() {
    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public boolean equalDownStation(Station station) {
        return this.downStation.equals(station);
    }

    public boolean equalUpStation(Station station) {
        return this.upStation.equals(station);
    }

    public boolean equalUpStation(Long upStationId) {
        return this.upStation.equalId(upStationId);
    }

    public boolean hasFinalDownStation(Long finalDownStationId) {
        return this.downStation.equalId(finalDownStationId);
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
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

    public Long getUpStationId() {
        return this.upStation.getId();
    }

    public Long getDownStationId() {
        return this.downStation.getId();
    }

    public boolean isLonger(int distance) {
        return this.distance > distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Section))
            return false;
        Section section = (Section)o;
        return Objects.equals(id, section.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
