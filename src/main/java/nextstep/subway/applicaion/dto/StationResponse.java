package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Station;

import java.time.LocalDateTime;

public class StationResponse {
    private final Long id;
    private final String name;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public StationResponse(Long id, String name, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public StationResponse(Station station) {
        this.id = station.getId();
        this.name = station.getName();
        this.createdDate = station.getCreatedDate();
        this.modifiedDate = station.getModifiedDate();
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
    public String toString() {
        return "StationResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }
}