package atdd.station.model;

import atdd.station.model.entity.Station;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateStationRequestView {
    private String name;

    public CreateStationRequestView() {
    }

    @Builder
    private CreateStationRequestView(String name) {
        this.name = name;
    }

    public Station toStation() {
        return Station.builder()
                .name(this.name).build();
    }
}
