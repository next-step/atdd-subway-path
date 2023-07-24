package nextstep.subway.station;

import com.fasterxml.jackson.annotation.JsonProperty;
import nextstep.subway.section.Section;

public class StationResponse {
    private Long id;
    private String name;

    public StationResponse(@JsonProperty("id")Long id, @JsonProperty("name") String name) {
        this.id = id;
        this.name = name;
    }

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
}
