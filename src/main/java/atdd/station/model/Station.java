package atdd.station.model;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Station {
    private long id;
    private String name;

    public Station(long id, String name){
        this.id = id;
        this.name = name;
    }
}
