package atdd.station.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class StationRequest implements Serializable {

    private static final long serialVersionUID = -5070453224736788466L;

    private String name;

    public StationRequest() {
    }

    public StationRequest(final String name) {
        this.name = name;
    }

    public Station toStation() {
        return new Station(name);
    }
}
