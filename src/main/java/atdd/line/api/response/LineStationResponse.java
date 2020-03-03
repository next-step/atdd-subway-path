package atdd.line.api.response;

import atdd.station.domain.Station;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
@Getter
public class LineStationResponse {

    private Long id;
    private String name;

    public LineStationResponse(Station station) {
        this.id = station.getId();
        this.name = station.getName();
    }

}
