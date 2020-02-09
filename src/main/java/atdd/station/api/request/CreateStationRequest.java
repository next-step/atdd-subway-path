package atdd.station.api.request;

import atdd.station.repository.Station;

import java.util.StringJoiner;

public class CreateStationRequest {

    private String name;

    protected CreateStationRequest() {}

    public String getName() {
        return name;
    }

    public Station toStation() {
        return new Station(name);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CreateStationRequest.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .toString();
    }

}
