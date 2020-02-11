package atdd.station.api.response;

import atdd.station.domain.Station;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.StringJoiner;

import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
@Getter
public class StationResponse {

    private Long id;
    private String name;

    public StationResponse(Station station) {
        this.id = station.getId();
        this.name = station.getName();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", StationResponse.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .toString();
    }

}
