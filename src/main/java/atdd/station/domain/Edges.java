package atdd.station.domain;

import org.hibernate.annotations.Where;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Edges {
    @OneToMany(mappedBy = "subwayLine", fetch = FetchType.EAGER)
    @Where(clause = "deleted = false")
    @OrderBy("id ASC")
    private List<Edge> edges = new ArrayList<>();

    public Edges() {
    }

    public Edges(List<Edge> edges) {
        this.edges = edges;
    }

    public List<Edge> getEdges() {
        return edges;
    }
}
