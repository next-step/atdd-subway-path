package nextstep.subway.station;

import javax.persistence.*;

@Entity
public class Station implements Comparable<Station> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 20, nullable = false)
    private String name;

    @Column(nullable = false)
    private long sequence = 0L;

    public Station() {
    }

    public Station(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(Station o) {
        return Long.compare(this.sequence, o.sequence);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Station) {
            return this.id.equals(((Station) obj).id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
}
