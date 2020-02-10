package atdd.station.domain;

import org.hibernate.annotations.Where;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import java.util.List;

@Embeddable
public class Stations {
    @ManyToMany(mappedBy = "subwayLines", fetch = FetchType.EAGER)
    @Where(clause = "deleted = false")
    @OrderBy("id ASC")
    private List<Station> stations;

    public Stations(List<Station> stations) {
        this.stations = stations;
    }
}


