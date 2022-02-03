package nextstep.subway.domain;

import javax.persistence.*;

@Entity
public class Section {
    private static final String INVALID_DISTANCE_MESSAGE = "신규 역 사이의 길이가 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없습니다.";

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

    public Section changeUpStation(Section otherSection) {
        validateDistance(otherSection.distance);
        return new Section(line, otherSection.downStation, downStation, distance - otherSection.distance);
    }

    public Section changeDownStation(Section otherSection) {
        validateDistance(otherSection.distance);
        return new Section(line, upStation, otherSection.upStation, distance - otherSection.distance);
    }

    public boolean equalsUpAndDownStation(Section section) {
        return hasSameUpStation(section) && hasSameDownStation(section);
    }

    public boolean hasSameUpStation(Section otherSection) {
        return upStation.equals(otherSection.upStation);
    }

    public boolean hasSameDownStation(Section otherSection) {
        return downStation.equals(otherSection.downStation);
    }

    public boolean isPreviousSection(Section otherSection) {
        return downStation.equals(otherSection.upStation);
    }

    public boolean isNextSection(Section otherSection) {
        return upStation.equals(otherSection.downStation);
    }

    private void validateDistance(int otherDistance) {
        if (distance <= otherDistance) {
            throw new IllegalArgumentException(INVALID_DISTANCE_MESSAGE);
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