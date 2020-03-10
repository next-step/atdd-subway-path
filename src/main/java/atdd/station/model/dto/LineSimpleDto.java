package atdd.station.model.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LineSimpleDto {
    private long id;
    private String name;

    public LineSimpleDto() {
    }

    @Builder
    private LineSimpleDto(long id, String name) {
        this.id = id;
        this.name = name;
    }
}
