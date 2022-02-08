package nextstep.subway.domain;

import javax.persistence.*;

@Entity
public class Station extends BaseEntity implements Comparable<Station> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;

    public Station() {
    }

    public Station(String name) {
        this.name = name;
    }

    public static Station of(String name) {
        return new Station(name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isEqualName(String name) {
        return this.name.equals(name);
    }

    @Override
    public int compareTo(Station o) {
        if (o.getId() < id) {
            return 1;
        } else if (o.getId() > id) {
            return -1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Station{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
