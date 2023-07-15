package nextstep.subway.dto;

import lombok.Getter;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

import java.util.List;

@Getter
public class LineResponse {

    private Long id;
    private String name;
    private String color;
    List<Station> stations;

    public static LineResponse fromEntity(Line line) {
        LineResponse lineResponse = new LineResponse();
        lineResponse.id = line.getId();
        lineResponse.name = line.getName();
        lineResponse.color = line.getColor();
        lineResponse.stations = line.getOrderedStationList();

        return lineResponse;
    }
}
