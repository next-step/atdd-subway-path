package nextstep.subway.applicaion.dto;

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
}
