package atdd.station.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class StationResponse implements Serializable {
    private static final long serialVersionUID = -7436535537405573126L;

    private Long id;

    @JsonProperty("name")
    private String name;

    public StationResponse() {
    }

    public StationResponse(final String name) {
        this.name = name;
    }

    public StationResponse(final Long id, final String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static StationResponse of(final Station station) {
        return new StationResponse(station.getId(), station.getName());
    }

    public static List<StationResponse> listOf(final List<Station> stations) {
        return stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }
}
