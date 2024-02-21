package nextstep.subway.controller.dto;

import lombok.Builder;
import lombok.Getter;
import nextstep.subway.domain.Line;

import java.util.List;

import static nextstep.subway.controller.dto.StationResponse.sectionsToStationResponses;

@Getter
public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    @Builder
    public LineResponse(Long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse lineToLineResponse(Line line) {
        return LineResponse.builder()
                .id(line.getId())
                .color(line.getColor())
                .name(line.getName())
                .stations(sectionsToStationResponses(line))
                .build();
    }
}
