package atdd.station.model.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Builder;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class LineDto {
    private long id;
    private String name;

    public LineDto() {
    }

    @Builder
    private LineDto(final long id, final String name) {
        this.id = id;
        this.name = name;
    }
}
