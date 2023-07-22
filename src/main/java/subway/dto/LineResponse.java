package subway.dto;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import subway.domain.Line;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LineResponse {

    private Long id;

    private String name;

    private String color;

    private Long distance;

    private List<StationResponse> stations;

    public static LineResponse from(Line line) {
        return LineResponse.builder()
            .id(line.getId())
            .name(line.getName())
            .color(line.getColor())
            .distance(line.getDistance())
            .stations(line.getSections().getAllStations().stream()
                .map(StationResponse::from)
                .collect(Collectors.toList())
            )
            .build();
    }

}
