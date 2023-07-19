package nextstep.subway.dto;

import java.util.List;
import java.util.stream.Collectors;
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

    public static LineResponse from(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(), line.getStations().stream().map(StationResponse::from).collect(Collectors.toList()));
    }
}
