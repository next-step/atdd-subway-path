package nextstep.subway.path.dto;

import nextstep.subway.station.domain.Station;

import java.time.LocalDateTime;
import java.util.Objects;

public class PathStationResponse {
    private Long id;
    private String name;
    private LocalDateTime createdDate;

    public static PathStationResponse of(Station station) {
        return new PathStationResponse(station.getId(), station.getName(), station.getCreatedDate());
    }

    public PathStationResponse() {
    }

    public PathStationResponse(Long id, String name, LocalDateTime createdDate) {
        this.id = id;
        this.name = name;
        this.createdDate = createdDate;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PathStationResponse station = (PathStationResponse) o;
        return Objects.equals(id, station.id) && Objects.equals(name, station.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
