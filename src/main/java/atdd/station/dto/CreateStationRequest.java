package atdd.station.dto;

import atdd.station.domain.Station;
import javax.validation.constraints.NotNull;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
public class CreateStationRequest {

    @NotNull
    private String name;

    public Station toEntry() {
        return Station.of(this.name);
    }

}
