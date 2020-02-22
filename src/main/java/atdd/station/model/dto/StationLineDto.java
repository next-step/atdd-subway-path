package atdd.station.model.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Builder;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class StationLineDto {
    private long id;
    private String name;

    public StationLineDto() {
    }

    @Builder
    private StationLineDto(final long id, final String name) {
        this.id = id;
        this.name = name;
    }
}
