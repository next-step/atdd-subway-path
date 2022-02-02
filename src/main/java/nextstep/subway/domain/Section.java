package nextstep.subway.domain;

import nextstep.subway.ui.exception.AddSectionException;

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

    void addLineBetweenSection(Section newSection) {
        if (isBetweenSection(newSection)) {
            validateDuplicationSection(newSection);
            validateDistance(newSection);
            this.upStation = newSection.getDownStation();
            this.distance -= newSection.getDistance();
        }
    }

    private boolean isBetweenSection(Section newSection) {
        return this.upStation.equals(newSection.getUpStation());
    }

    private void validateDuplicationSection(Section newSection) {
        if (this.upStation.equals(newSection.upStation) && this.downStation.equals(newSection.getDownStation())) {
            throw new AddSectionException(
                    String.format("상행역과 하행역 모두 등록된 역입니다. 상행역 = %s, 하행역 = %s",
                            this.upStation.getName(), this.downStation.getName()));
        }
    }

    private void validateDistance(Section newSection) {
        if (this.distance <= newSection.distance) {
            throw new AddSectionException(
                    String.format("새로 추가되는 구간 거리는 기존 구간의 거리 이상일 수 없습니다. 기존 구간 거리 = %d, 신규 구간 거리 = %d",
                            this.distance, newSection.distance));
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
        return distance;
    }
}