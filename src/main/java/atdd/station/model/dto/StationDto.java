package atdd.station.model.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class StationDto {
    private long id;
    private String name;

    public StationDto() {
    }

    @Builder
    private StationDto(final long id, final String name) {
        this.id = id;
        this.name = name;
    }
}
