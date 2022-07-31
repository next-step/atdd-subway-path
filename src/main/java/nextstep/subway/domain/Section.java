package nextstep.subway.domain;

import nextstep.subway.exception.DistanceException;

import java.util.List;

import javax.persistence.*;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    private int distance;

    public Section() {

    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public Station getUpStation() {
        return this.upStation;
    }

    public Station getDownStation() {
        return this.downStation;
    }

    public List<Station> getStationsAsc() {
        return List.of(this.upStation, this.downStation);
    }

    public boolean hasStation(Station station) {
        return this.upStation.isEqualTo(station) || this.downStation.isEqualTo(station);
    }

    public int getDistance() {
        return distance;
    }

    public void setUpStation(Station upStation) {
        this.upStation = upStation;
    }

    public void setDownStation(Station downStation) {
        this.downStation = downStation;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void addStationBetweenExistsStations(Section section, boolean isUpStationExists) {
        // 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음
        if (section.distance >= this.distance) {
            throw new DistanceException();
        }

        this.distance -= section.distance;
        if (isUpStationExists) {
            this.upStation = section.downStation;
        } else {
            this.downStation = section.upStation;
        }
    }

    public void merge(Section otherSection, Station deleteStation) {
        Station otherStation = otherSection.getStationsAsc().stream().filter(s -> !s.isEqualTo(deleteStation)).findAny().orElseThrow(IllegalArgumentException::new);

        if (this.upStation.isEqualTo(deleteStation)) {
            this.upStation = otherStation;
        } else {
            this.downStation = otherStation;
        }
        this.distance = this.distance + otherSection.distance;
    }

    public boolean compareValues(Section other) {
        return other != null &&
                this.upStation.compareValue(other.upStation) &&
                this.downStation.compareValue(other.downStation) &&
                this.distance == other.distance;
    }
}
