package nextstep.subway.line;

import nextstep.subway.line.section.Section;
import nextstep.subway.line.section.Sections;
import nextstep.subway.station.Station;
import javax.persistence.*;
import java.util.List;
import java.util.Optional;


@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Station getStartStation() {
        return startStation;
    }

    public Station getEndStation() {
        return endStation;
    }

    @Column(length = 20, nullable = false)
    private String name;

    @Column(length = 20, nullable = false)
    private String color;

    @ManyToOne
    @JoinColumn(name = "start_station_id", nullable = false)
    private Station startStation;

    @ManyToOne
    @JoinColumn(name = "end_station_id", nullable = false)
    private Station endStation;

    @Embedded
    private final Sections sections = new Sections();


    public Line() {
    }

    public Line(String name, String color, Station startStation, Station endStation) {
        this.name = name;
        this.color = color;
        this.startStation = startStation;
        this.endStation = endStation;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Sections getSections() {
        return sections;
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public boolean contains(Section section) {
        return getSections().contains(section);
    }

    public void deleteSection(Section section) {
        getSections().deleteSection(section);
    }

    public void addSection(Section section) {
        getSections().addSection(section);
    }

    public void removeSection(Section section) {
        getSections().removeSection(section);
    }

    public void addStationBefore(Station newStation, Station nextStation, int distance) {
        getSections().addStationBefore(newStation, nextStation, distance, this);
    }

    public void setStartStation(Station startStation) {
        this.startStation = startStation;
    }

    public void removeStation(Station newStation) {
        getSections().removeStation(newStation, this);
    }

}
