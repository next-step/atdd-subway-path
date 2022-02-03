package nextstep.subway.domain.section;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

public class SectionMultigraph<V, E> extends WeightedMultigraph<V, E> {

    public SectionMultigraph(Class<? extends E> edgeClass) {
        super(edgeClass);
    }

    public void setEdge(E e, double weight) {
        assert (e instanceof DefaultWeightedEdge) : e.getClass();

        ((SectionEdge) e).weight = weight;
    }
}
