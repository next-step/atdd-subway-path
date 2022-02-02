package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Station;

import java.time.LocalDateTime;

public class PathStationResponse {
    private Long id;
    private String name;
    private LocalDateTime createdAt;

    public PathStationResponse() {
    }

    public PathStationResponse(Long id, String name, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    public PathStationResponse(Station station) {
        id = station.getId();
        name = station.getName();
        createdAt = station.getCreatedDate();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

}
