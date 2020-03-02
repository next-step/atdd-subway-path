package atdd.path.domain;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.util.List;

import static javax.persistence.GenerationType.*;

@Entity
@Getter
public class Station {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String name;

    public Station() {
    }

    public Station(String name) {
        this.name = name;
    }

    @Builder
    public Station(Long id, String name) {
        this.id = id;
        this.name = name;
    }

}

