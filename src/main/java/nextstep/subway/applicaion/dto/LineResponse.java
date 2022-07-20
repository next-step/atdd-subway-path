package nextstep.subway.applicaion.dto;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    public LineResponse(Long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }
}

