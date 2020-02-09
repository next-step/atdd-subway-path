package atdd.station.api.response;

import atdd.station.repository.Station;

import java.util.StringJoiner;

public class StationResponse {

    private Long id;
    private String name;

    protected StationResponse() {}

    public StationResponse(Station station) {
        this.id = station.getId();
        this.name = station.getName();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", StationResponse.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .toString();
    }

}
