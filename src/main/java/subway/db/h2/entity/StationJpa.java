package subway.db.h2.entity;

import javax.persistence.*;

@Entity
@Table(name = "stations")
public class StationJpa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "station_id")
    private Long id;
    @Column(length = 20, nullable = false)
    private String name;

    public StationJpa() {
    }

    public StationJpa(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
