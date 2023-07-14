package nextstep.subway.line.view;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.view.StationResponse;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    private LineResponse(Line line) {
        this.id = line.getId();
        this.name = line.getName();
        this.color = line.getColor();
        this.stations = List.of(new StationResponse(line.getUpStation()), new StationResponse(line.getDownStation()));
    }

    public static LineResponse from(Line line) {
        return new LineResponse(line);
    }
}
