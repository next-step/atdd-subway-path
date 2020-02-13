package atdd.station.model;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Station {
    private long id;
    private String name;

    public Station() {
    }

    @Builder
    public Station(long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\"=" + id +
                ", \"name\"=\"" + name + '"' +
                '}';
    }
}
