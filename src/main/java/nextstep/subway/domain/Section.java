package nextstep.subway.domain;

import javax.persistence.*;
import lombok.Getter;

@Entity
@Getter
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

    public boolean isUpStation(Station station) {
        return upStation.equals(station);
    }

    public boolean isDownStation(Station station) {
        return downStation.equals(station);
    }

    public void updateWhenSectionAddedInMiddle(Section section) {
        if (distance <= section.getDistance()) {
            throw new IllegalArgumentException("구간의 길이가 너무 깁니다.");
        }
        if (section.getUpStation().equals(upStation)) {
            updateWhenSectionAddedInMiddle(section.getDownStation(), downStation,
                distance - section.getDistance());
        }
        if (section.getDownStation().equals(downStation)) {
            updateWhenSectionAddedInMiddle(upStation, section.getUpStation(),
                distance - section.getDistance());
        }
    }

    private void updateWhenSectionAddedInMiddle(Station upStation, Station downStation,
        int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }
}