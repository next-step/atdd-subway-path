package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.exception.ErrorType;
import nextstep.subway.exception.SectionDeleteException;
import org.jgrapht.graph.WeightedMultigraph;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;
    @Embedded
    private Sections sections = new Sections();

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        sections.addSection(new Section(this, upStation, downStation, distance));
    }

    public void removeSection(Station station) {
        if (sections.remainOneSection()) {
            throw new SectionDeleteException(ErrorType.CANNOT_REMOVE_LAST_SECTION);
        }
        sections.remove(station);
    }

    public void updateName(String name) {
        if (name != null) {
            this.name = name;
        }
    }

    public void updateColor(String color) {
        if (color != null) {
            this.color = color;
        }
    }

    public List<Section> getSections() {
        return sections.getSections();
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public void addVertexAndEdge(WeightedMultigraph graph) {
        getStations().forEach(graph::addVertex);
        sections.addEdges(graph);
    }
}
