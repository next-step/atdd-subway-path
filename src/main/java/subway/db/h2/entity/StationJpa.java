package subway.db.h2.entity;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "stations")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StationJpa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "station_id")
    private Long id;
    @Column(length = 20, nullable = false)
    private String name;

    public static StationJpa of(String name) {
        return new StationJpa(name);
    }

    public static StationJpa of(Long id, String name) {
        return new StationJpa(id, name);
    }

    private StationJpa(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    private StationJpa(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isNew() {
        return id == null;
    }
}
