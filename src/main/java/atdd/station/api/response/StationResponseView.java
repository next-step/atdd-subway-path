package atdd.station.api.response;

import atdd.station.domain.Station;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@Getter
public class StationResponseView {

    private Long id;
    private String name;

    public StationResponseView(Station station) {
        this.id = station.getId();
        this.name = station.getName();
    }

}
