package atdd.station.api.response;

import atdd.line.domain.Line;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
@Getter
public class StationLineResponse {

    private Long id;
    private String name;

    public StationLineResponse(Line line) {
        this.id = line.getId();
        this.name = line.getName();
    }

}
