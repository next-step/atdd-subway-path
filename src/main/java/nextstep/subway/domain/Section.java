package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = new Distance(distance);
    }

    public boolean isSameSection(Section newSection) {
        return this.getUpStation().equals(newSection.getUpStation())
                && this.getUpStation().equals(newSection.getDownStation());
    }

    public boolean isSameDownStation(Station downStation) {
        return this.getDownStation() == downStation;
    }

    public boolean isSameUpStation(Station upStation) {
        return this.getUpStation() == upStation;
    }

    public boolean isMoreLongerThan(Section section) {
        return this.distance.isMoreLongerThan(section.distance);
    }

    public int minusDistance(Section target) {
        return this.distance.minus(target.distance);
    }
}