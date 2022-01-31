package nextstep.subway.domain;

import nextstep.subway.exception.IllegalSectionArgumentException;

import javax.persistence.*;

@Entity
public class Section {
    public static final String NOT_NULL = "distance 값이 없거나 0으로 올수 없습니다.";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    private int distance;


    protected Section() {}

    public Section(Line line, Station upStation, Station downStation, int distance) {
        validateDistance(distance);

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
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }

    public boolean isUpStation(Station station) {
        return this.upStation.equals(station);
    }

    public boolean isDownStation(Station station) {
        return this.downStation.equals(station);
    }

    public boolean isLast(Station station) {
        return this.downStation.equals(station);
    }

    public boolean isFirstStation(Station station) {
        return this.upStation.equals(station);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void validationDownStation(Section section) {
        if (this.downStation == section.getDownStation() && this.upStation == section.getDownStation()) {
            throw new IllegalSectionArgumentException("추가되는 구간의 상행역 과 하행역이 현재 등록되어있는 역일 수 없습니다.");
        }
    }

    private void validateDistance(int distance) {
        if (distance == 0) {
            throw new IllegalSectionArgumentException(NOT_NULL);
        }
    }
}