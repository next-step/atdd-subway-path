package nextstep.subway.domain;

import nextstep.subway.exception.IllegalUpdatingStateException;

import javax.persistence.*;

@Entity
public class Section extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Line line;
    @ManyToOne(fetch = FetchType.LAZY)
    private Station upStation;
    @ManyToOne(fetch = FetchType.LAZY)
    private Station downStation;
    private int distance;

    public Section() {
    }

    public Section(Long id, Line line, Station upStation, Station downStation, int distance) {
        this.id = id;
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public void updateLine(Line line) {
        this.line = line;
    }

    public void updateForSplittingBySameUpStationSection(Section sameUpStationSection) {
        decreaseDistance(sameUpStationSection.getDistance());
        this.upStation = sameUpStationSection.getDownStation();
    }

    public void updateForSplittingBySameDownStationSection(Section sameDownStationSection) {
        decreaseDistance(sameDownStationSection.getDistance());
        this.downStation = sameDownStationSection.getUpStation();
    }

    private void decreaseDistance(int distance) {
        if (distance >= this.distance) {
            throw new IllegalUpdatingStateException("구간의 길이가 감소할 길이보다 작거나 같습니다."
                    + " section.id:" + this.getId() + "\n"
                    + " section.distance:" + this.getDistance());
        }
        this.distance -= distance;
    }

    public void updateDownStation() {
        this.downStation = downStation;
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


}
