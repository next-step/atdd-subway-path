package nextstep.subway.domain;

import nextstep.subway.exception.AddSectionException;

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

    protected Section() {

    }

    public Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section of(Station upStation, Station downStation, int distance) {
        return new Section(upStation, downStation, distance);
    }

    public void changeExistingUpStationToNewDownStation(Section newSection) {
        validateSectionDistance(newSection);
        this.upStation = newSection.downStation;
        this.distance -= newSection.distance;
    }

    public void changeExistingDownStationToNewUpStation(Section newSection) {
        validateSectionDistance(newSection);
        this.downStation = newSection.upStation;
        this.distance -= newSection.distance;
    }

    private void validateSectionDistance(Section newSection) {
        if (newSection.distance >= this.distance) {
            throw new AddSectionException("신규 구간의 길이는 기존 구간의 길이보다 짧아야 합니다.");
        }
    }

    public void line(Line line) {
        this.line = line;
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