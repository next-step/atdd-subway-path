package atdd.station.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Station {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(nullable = false)
    private String name;

    private Station(String name) {
        this.name = name;
    }

    private Station(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Station of(long id, String name) {
        return new Station(id, name);
    }

    public static Station of(String name) {
        return new Station(name);
    }


}
