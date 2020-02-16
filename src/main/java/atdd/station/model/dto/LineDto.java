package atdd.station.model.dto;

import lombok.Builder;

public class LineDto {
    private long id;
    private String name;

    @Builder
    private LineDto(final long id, final String name) {
        this.id = id;
        this.name = name;
    }
}
