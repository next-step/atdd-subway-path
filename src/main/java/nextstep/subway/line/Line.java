package nextstep.subway.line;

import nextstep.subway.section.Section;
import nextstep.subway.station.Station;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "line")
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 20, nullable = false)
    private String name;

    @Column(length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private Color color;

    @ManyToOne(fetch = FetchType.LAZY)
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @Column(nullable = false)
    private Integer distance;

    protected Line() {}


    public Line(String name, Color color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Integer getDistance() {
        return distance;
    }

    public void update(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public boolean isLastStation(Station station) {
        return downStation.equals(station);
    }


    public void removeSection(Section section) {
        if(!isLastStation(section.getDownStation())) {
            throw new IllegalStateException("하행 종점만 제거 가능");
        }
        changeDownStation(section.getUpStation());
    }

    public void addSection(Section section) {
        this.downStation = section.getDownStation();
        this.distance += section.getDistance();
    }

    public void changeDownStation(Station upStation) {
        this.downStation = upStation;
    }

    public List<Station> getStations() {
        return List.of(upStation, downStation);
    }
}
