package nextstep.subway.line.dto.response;

import lombok.Builder;
import lombok.Getter;
import nextstep.subway.line.entity.Line;
import nextstep.subway.station.dto.response.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
public class LineResponse {

    private Long id;

    private String name;

    private String color;

    private List<StationResponse> stations;

    public static LineResponse of(Line line) {
        return LineResponse.builder()
                .id(line.getId())
                .name(line.getName())
                .color(line.getColor())
                .stations(line
                        .getSections()
                        .getAllStations()
                        .stream()
                        .map(StationResponse::of)
                        .collect(Collectors.toList()))
                .build();
    }

}
