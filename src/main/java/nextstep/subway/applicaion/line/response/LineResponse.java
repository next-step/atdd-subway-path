package nextstep.subway.applicaion.line.response;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.applicaion.station.response.StationResponse;
import nextstep.subway.domain.line.Line;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    public LineResponse(final Long id, final String name, final String color, final List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse toResponse(final Line line) {
        return new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                StationResponse.toResponses(line.getStations())
        );
    }

    public static List<LineResponse> toResponses(final List<Line> lines) {
        return lines.stream()
                .map(LineResponse::toResponse)
                .collect(Collectors.toUnmodifiableList());
    }
}
