package nextstep.subway.applicaion.dto;

import lombok.RequiredArgsConstructor;
import nextstep.subway.domain.Station;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class StationResponse {
    private final Long id;
    private final String name;
    private final LocalDateTime createdDate;
    private final LocalDateTime modifiedDate;

    public static StationResponse of(Station station) {
        return new StationResponse(
                station.getId(),
                station.getName(),
                station.getCreatedDate(),
                station.getModifiedDate()
        );
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

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }
}
