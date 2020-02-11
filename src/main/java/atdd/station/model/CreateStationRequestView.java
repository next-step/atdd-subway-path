package atdd.station.model;

import lombok.Setter;

@Setter
public class CreateStationRequestView {
    private String name;

    public Station toStation() {
        return Station.builder()
                .name(this.name).build();
    }
}
