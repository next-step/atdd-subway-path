package nextstep.subway.domain;

import nextstep.subway.ui.exception.SectionException;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
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

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @Embedded
    private Distance distance;

    public Section() {

    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = new Distance(distance);
    }

    public void updateAddLineBetweenSection(Section newSection) {
        if (isBetweenSection(newSection)) {
            validateDuplicationSection(newSection);
            this.upStation = newSection.getDownStation();
            this.distance = this.distance.subtract(newSection.getDistance());
        }
    }

    public void updateRemoveLineBetweenSection(Section removeSection) {
        if (upStation.equals(removeSection.getDownStation())) {
            this.upStation = removeSection.getUpStation();
            this.distance = this.distance.sum(removeSection.getDistance());
        }
    }

    private boolean isBetweenSection(Section newSection) {
        return this.upStation.equals(newSection.getUpStation());
    }

    private void validateDuplicationSection(Section newSection) {
        if (this.upStation.equals(newSection.upStation) && this.downStation.equals(newSection.getDownStation())) {
            throw new SectionException(
                    String.format("상행역과 하행역 모두 등록된 역입니다. 상행역 = %s, 하행역 = %s",
                            this.upStation.getName(), this.downStation.getName()));
        }
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
        return distance.getDistance();
    }
}