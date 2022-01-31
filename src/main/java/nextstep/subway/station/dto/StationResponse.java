package nextstep.subway.station.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.station.domain.Station;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class StationResponse {
    private Long id;
    private String name;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public StationResponse(Long id, String name, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static StationResponse of(Station station) {
        return new StationResponse(station.getId(), station.getName(),
                station.getCreatedDate(), station.getModifiedDate());
    }
}
