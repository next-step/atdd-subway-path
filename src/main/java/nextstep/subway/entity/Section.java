package nextstep.subway.entity;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "station_line_id")
    private Line line;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @Column(nullable = false)
    private int distance;

    public Section() {
    }

    public Section(Line line, Station upStation, Station downStation,
        int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section of(Line line, Station upStation, Station downStation, int distance) {
        return new Section(line, upStation, downStation, distance);
    }

    public Long getId() {
        return id;
    }

    public Long getUpStationId() {
        return upStation.getId();
    }

    public int getDistance() {
        return distance;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Long getDownStationId() {
        return downStation.getId();
    }

    public List<Long> getStationIdList() {
        return List.of(upStation.getId(), downStation.getId());
    }

    public boolean isEqualsDownStation(long downStationId) {
        return this.downStation.getId().equals(downStationId);
    }

    public boolean isEqualsUpStation(long stationId) {
        return this.upStation.getId().equals(stationId);
    }

    public void validationAddDistance(int distance) {
        if (this.distance <= distance){
            throw new IllegalArgumentException("추가할려는 구간이 기존 구간 길이와 같거나 더 깁니다.");
        }
    }

    public void changeUpStation(Station changeStation) {
        this.upStation = changeStation;
    }

    public void changeDownStation(Station changeStation) {
        this.downStation = changeStation;
    }

    public void minusDistance(int distance) {
        this.distance -= distance;
    }
}
