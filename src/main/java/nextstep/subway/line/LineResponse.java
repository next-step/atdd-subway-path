package nextstep.subway.line;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import nextstep.subway.station.StationResponse;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
@Builder
public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private int distance;
    private List<StationResponse> stations;

    public static LineResponse from(Line line) {
        List<StationResponse> stations = line.getSections().stream()
                .flatMap(section -> Stream.of(section.getUpstation(), section.getDownstation()))
                .distinct()
                .map(StationResponse::from)
                .collect(Collectors.toList());

        return LineResponse.builder()
                .id(line.getId())
                .name(line.getName())
                .color(line.getColor())
                .distance(line.getSections().getTotalDistance())
                .stations(stations)
                .build();
    }

}
