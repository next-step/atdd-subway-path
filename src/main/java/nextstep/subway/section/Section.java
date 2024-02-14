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
        System.out.println();
        System.out.println("(isConnectedSection)");
        System.out.println("this = " + this);
        System.out.println("section = " + section);
        System.out.println();

        /**
         * 1. 상행이 같아서 뒤에 연결해야 하는 경우
         * 2. 하행이 같아서 앞에 연결해야 하는 경우 (위와 동일한 인덱스)
         */
        return isConnectedUpStation(section) || isConnectedDownStation(section);
    }

    private boolean isConnectedUpStation(Section section) {
        return this.upStation == section.upStation;
    }

    private boolean isConnectedDownStation(Section section) {
        return this.downStation == section.downStation;
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

    public boolean isFirstSection(Section firstSection) {
        System.out.println("(isFirstSection)");
        System.out.println("this = " + this);
        System.out.println("firstSection = " + firstSection);
        System.out.println();
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
}
