package nextstep.subway.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Getter
@NoArgsConstructor
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

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public boolean containsUpStation(Section section) {
        return this.getUpStation().equals(section.getUpStation()) || this.getDownStation().equals(section.getUpStation());
    }

    public boolean containsDownStation(Section section) {
        return this.getUpStation().equals(section.getDownStation()) || this.getDownStation().equals(section.getDownStation());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(getId(), section.getId()) && Objects.equals(getUpStation(), section.getUpStation()) && Objects.equals(getDownStation(), section.getDownStation()) && Objects.equals(getDistance(), section.getDistance()) && Objects.equals(getLine(), section.getLine());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUpStation(), getDownStation(), getDistance(), getLine());
    }

    public boolean isContainStation(Station station) {
        return upStation.equals(station) || downStation.equals(station);
    }
}