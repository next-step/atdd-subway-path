package atdd.domain;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.Entity;

@Getter
@Entity
public class Station {
    private Long id;
    private String name;

    public Station() {
    }

    @Builder
    public Station(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
