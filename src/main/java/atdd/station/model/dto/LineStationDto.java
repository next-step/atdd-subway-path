package atdd.station.model.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LineStationDto {
    private long id;
    private String name;

    public LineStationDto() {
    }

    @Builder
    private LineStationDto(final long id, final String name) {
        this.id = id;
        this.name = name;
    }
}
