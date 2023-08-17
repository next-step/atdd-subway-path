package subway.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jgrapht.graph.DefaultWeightedEdge;

@AllArgsConstructor
@Getter
public class SectionEdge extends DefaultWeightedEdge {

    private Section section;

    public static SectionEdge of(Section section) {
        return new SectionEdge(section);
    }

}
