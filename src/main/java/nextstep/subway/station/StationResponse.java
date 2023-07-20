package nextstep.subway.station;

import java.util.Objects;

public class StationResponse {
    private Long id;
    private String name;

    public StationResponse() {}

    public StationResponse(Station station) {
        this(station.getId(), station.getName());
    }

    public StationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StationResponse that = (StationResponse) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getName(),
            that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName());
    }
}
