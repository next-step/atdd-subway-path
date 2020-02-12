package atdd.station.model;

import lombok.Builder;
import lombok.Setter;

@Setter
public class CreateStationRequestView {
    private String name;

    @Builder
    private CreateStationRequestView(String name) {
        this.name = name;
    }

    public Station toStation() {
        return Station.builder()
                .name(this.name).build();
    }
}
