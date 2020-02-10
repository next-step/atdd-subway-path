package atdd.station.domain;

import org.hibernate.annotations.Where;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import java.util.List;

@Embeddable
public class SubwayLines {
    @ManyToMany(mappedBy = "subwayLines", fetch = FetchType.EAGER)
    @Where(clause = "deleted = false")
    @OrderBy("id ASC")
    private List<SubwayLine> subwayLines;

    public SubwayLines(List<SubwayLine> subwayLines) {
        this.subwayLines = subwayLines;
    }
}


