package nextstep.subway.station.dto;

import nextstep.subway.station.Station;

import java.util.Objects;

public class
StationResponse {
    private Long id;
    private String name;

    private StationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static StationResponse ofEntity(Station station) {
        return new StationResponse(station.getId(), station.getName());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StationResponse that = (StationResponse) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
