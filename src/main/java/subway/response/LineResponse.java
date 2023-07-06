package subway.response;


import lombok.Builder;
import lombok.Getter;
import subway.entity.Station;

import java.util.List;

@Getter
public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<Station> stations;

    @Builder
    public LineResponse(Long id, String name, String color, List<Station> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }
}
