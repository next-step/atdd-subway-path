package nextstep.subway.line;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import nextstep.subway.station.Station;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 20, nullable = false)
    private String name;
    private String color;
    @Embedded
    private Sections sections;

    public Line() {
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;
        this.sections = new Sections(upStation, downStation, distance);
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

    public List<Station> getStations() {
        return sections.getStations();
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void deleteSection(Station bottomStation) {
        sections.deleteSection(bottomStation);
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        sections.addSection(new Section(upStation, downStation, distance));
    }

    public void putWeightedMultiGraph(WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        sections.putWeightedMultiGraph(graph);
    }
}
