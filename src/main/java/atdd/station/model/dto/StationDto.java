package atdd.station.model.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class StationDto {
    private long id;
    private String name;
    private List<LineSimpleDto> lines = new ArrayList<>();

    public StationDto() {
    }

    @Builder
    private StationDto(long id, String name, List<LineSimpleDto> lines) {
        this.id = id;
        this.name = name;
        this.lines = lines;
    }
}
