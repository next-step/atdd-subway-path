package atdd.station.model.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class StationSimpleDto {
    private long id;
    private String name;

    public StationSimpleDto() {
    }

    @Builder
    private StationSimpleDto(long id, String name) {
        this.id = id;
        this.name = name;
    }
}
