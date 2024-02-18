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
        return isConnectedUpStation(section) || isConnectedDownStation(section) || connectedStation(section);
    }

    /**
     * 현재 구간의 상행역과 다른 구간의 하행역이 같은지 확인
     */
    private boolean isConnectedUpStation(Section section) {
        return this.upStation == section.upStation;
    }

    /**
     * 현재 구간의 하행역과 다른 구간의 하행역이 같은지 확인
     */
    private boolean isConnectedDownStation(Section section) {
        return this.downStation == section.downStation;
    }

    /**
     * 마지막 구간에서 추가로 이어지는지 확인
     */
    private boolean connectedStation(Section section) {
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

    public boolean isFirstSection(Section firstSection) {
        return this.downStation == firstSection.upStation;
    }

    public boolean isFinalSection(Section lastSection) {
        return lastSection.getDownStation() == upStation;
    }

    public void updateDownStation(Station newUpStation) {
        this.downStation = newUpStation;
    }

    public void updateUpStation(Station newDownStation) {
        this.upStation = newDownStation;
    }

    public boolean isAlreadyRegisteredSection(Section anotherSection) {
        return isExactlyRegisteredSection(anotherSection) || isReverseRegisteredSection(anotherSection);
    }

    private boolean isExactlyRegisteredSection(Section anotherSection) {
        return this.upStation == anotherSection.upStation && this.downStation == anotherSection.downStation;
    }

    private boolean isReverseRegisteredSection(Section anotherSection) {
        return this.upStation == anotherSection.downStation && this.downStation == anotherSection.upStation;
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
