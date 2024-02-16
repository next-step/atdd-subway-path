package nextstep.subway.applicaion.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import nextstep.subway.domain.Line;

@Getter
@AllArgsConstructor
public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    public static LineResponse from(Line line, List<StationResponse> stations) {
        return new LineResponse(
            line.getId(),
            line.getName(),
            line.getColor(),
            stations
        );
    }
}

