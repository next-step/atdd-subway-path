package atdd.path.application.dto;

import atdd.path.domain.Station;
import lombok.Getter;

@Getter
public class StationRequestView {
    private String name;

    public StationRequestView() {
    }

    public StationRequestView(String name) {
        this.name = name;
    }

    public Station toStation(){
        return Station.builder()
                .name(name)
                .build();
    }
}
