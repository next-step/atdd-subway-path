package atdd.station.api.response;

import atdd.line.domain.Line;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.StringJoiner;

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

    @Override
    public String toString() {
        return new StringJoiner(", ", StationLineResponse.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .toString();
    }

}
