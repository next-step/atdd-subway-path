package atdd.station.domain;

import lombok.Builder;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Size(min = 2, max = 20)
    private String name;

    @Size(min = 2, max = 20)
    private String beforeStation;

    @Size(min = 2, max = 20)
    private String afterStation;

    @OneToMany(mappedBy = "subwayLine", fetch = FetchType.EAGER)
    @Where(clause = "deleted = false")
    @OrderBy("id ASC")
    private List<Subway> subways = new ArrayList<>();

    private boolean deleted = false;

    public Station() {
    }

    public Station(String name) {
        this.name = name;
    }

    @Builder
    public Station(String name, List<Subway> subways) {
        this.name = name;
        this.subways = subways;
    }

    public long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public List<Subway> getSubways() {
        return this.subways;
    }

    public List<SubwayLine> getSubwayLines() {
        return this.subways.stream()
                .map(Subway::getSubwayLine)
                .collect(Collectors.toList());
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


