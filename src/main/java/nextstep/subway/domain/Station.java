package nextstep.subway.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    public Station(String name) {
        this.id = null;
        this.name = name;
    }

    public Station(final Long id, final String name) {
        this.id = id;
        this.name = name;
    }

    public Station() {

    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
