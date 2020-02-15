package atdd.station.api.request;

import atdd.station.domain.Station;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.StringJoiner;

import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
@Getter
public class CreateStationRequestView {

    private String name;

    public Station toStation() {
        return new Station(name);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CreateStationRequestView.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .toString();
    }

}
