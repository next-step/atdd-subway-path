package nextstep.subway.station.application.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import nextstep.subway.station.domain.Station;

@Getter
public class StationResponse {
    private final Long id;
    private final String name;
    private final LocalDateTime createdDate;
    private final LocalDateTime modifiedDate;

    @Builder
    private StationResponse(Long id, String name, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static StationResponse from(Station station) {
        return builder()
            .id(station.getId())
            .name(station.getName())
            .createdDate(station.getCreatedDate())
            .modifiedDate(station.getModifiedDate())
            .build();
    }
}
