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

    private int distance;

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public boolean isSameSection(Section newSection) {
        return this.getUpStation() == newSection.getUpStation()
                && this.getDownStation() == newSection.getDownStation();
    }

    public boolean isSameDownStation(Station downStation) {
        return this.getDownStation() == downStation;
    }

    public boolean isSameUpStation(Station upStation) {
        return this.getUpStation() == upStation;
    }

    public boolean isLongerThan(Section section) {
        return section.getDistance() - this.getDistance() <= 0;
    }
}