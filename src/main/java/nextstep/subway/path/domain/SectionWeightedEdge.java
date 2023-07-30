package nextstep.subway.path.domain;

import nextstep.subway.section.entity.Section;
import nextstep.subway.station.entity.Station;
import org.jgrapht.graph.DefaultWeightedEdge;

public class SectionWeightedEdge extends DefaultWeightedEdge {

    private final Section section;

    public SectionWeightedEdge(Section section) {
        this.section = section;
    }

    public Section getSection() {
        return section;
    }

    public Station getSource() {
        return section.getUpStation();
    }

    public Station getTarget() {
        return section.getDownStation();
    }

    public double getWeight() {
        return section.getDistance();
    }
}
