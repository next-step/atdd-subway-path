package atdd.station.model.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class IdNameDto {
    private long id;
    private String name;

    public IdNameDto() {
    }

    @Builder
    private IdNameDto(long id, String name) {
        this.id = id;
        this.name = name;
    }
}
