package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

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

    @ManyToOne(fetch = FetchType.EAGER)
    private Station upStation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @Embedded
    private LineSections lineSections = new LineSections();

    @Column(nullable = false)
    private Integer distance;

    protected Line() {}


    public Line(String name, Color color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        lineSections.add(new Section(upStation, downStation, distance, this));
    }

    public void update(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public void addSection(Section section) {
        lineSections.add(section);
    }

    public void removeSection(long stationsId) {
        Optional<Section> section = lineSections.find(stationsId);
        section.ifPresent(value -> lineSections.remove(value));
    }

    public void removeSection(Section section) {
        lineSections.remove(section);
    }

    public List<Station> getStations() {
        return lineSections.getStations();
    }


    public boolean deletableSection() {
        return lineSections.deletable();
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
}
