package atdd.station.domain;

import lombok.Builder;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Size(min = 2, max = 20)
    private String name;

    @OneToMany(mappedBy = "subwayLine", fetch = FetchType.EAGER)
    @Where(clause = "deleted = false")
    @OrderBy("id ASC")
    private List<Subway> subwayLine;

    private boolean deleted = false;

    public Station() {
    }

    @Builder
    public Station(String name) {
        this.name = name;
    }

    public long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return "Station{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public void deleteStation() {
        this.deleted = true;
    }

    public boolean isDeleted() {
        return this.deleted;
    }
}


