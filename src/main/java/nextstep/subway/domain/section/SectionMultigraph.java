package nextstep.subway.domain.section;

import java.io.InvalidClassException;
import nextstep.subway.exception.InvalidPathSearchingException;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

public class SectionMultigraph<V, E> extends WeightedMultigraph<V, E> {

    public SectionMultigraph(Class<? extends E> edgeClass) {
        super(edgeClass);
    }

    public void setEdge(E e, double weight) {
        if (!(e instanceof DefaultWeightedEdge)) {
            throw new InvalidPathSearchingException();
        }

        ((SectionEdge) e).weight = weight;
    }
}
