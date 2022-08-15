package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.List;

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

    public void updateUpStation(Station station, int distance) {
        this.upStation = station;
        this.distance = distance;
    }

    public void updateDownStation(Station station, int distance) {
        this.downStation = station;
        this.distance = distance;
    }

    // 상행종점역이 포함된 구간인지 여부
    public boolean isSectionWithLastUpStation(List<Section> sections) {
        return sections.stream()
                .noneMatch((section -> section.getDownStation().equals(upStation)));
    }

    // 하행종점역이 포함된 구간인지 여부
    public boolean isSectionWithLastDownStation(List<Section> sections) {
        return sections.stream()
                .noneMatch((section -> section.getUpStation().equals(downStation)));
    }


    public boolean matchStations(Station upStation, Station downStation) {
        return this.getUpStation().equals(upStation)
                && this.getDownStation().equals(downStation);
    }
}