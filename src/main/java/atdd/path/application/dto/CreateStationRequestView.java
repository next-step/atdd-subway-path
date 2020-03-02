package atdd.path.application.dto;

import atdd.path.domain.Station;
import lombok.Getter;

@Getter
public class CreateStationRequestView {
    private String name;

    public CreateStationRequestView() {
    }

    public CreateStationRequestView(String name) {
        this.name = name;
    }

    public Station toStation(){
        return Station.builder()
                .name(name)
                .build();
    }
}
