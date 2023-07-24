package nextstep.subway.line.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import nextstep.subway.line.repository.Line;
import nextstep.subway.station.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

@Value
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LineResponse {
    Long id;
    String name;
    String color;
    List<StationResponse> stations;

    public static LineResponse from(Line line) {
        return LineResponse.builder()
                .id(line.getId())
                .name(line.getName())
                .color(line.getColor())
                .stations(line.getSections().getAllStation()
                        .stream()
                        .map(station -> new StationResponse(station.getId(), station.getName()))
                        .collect(Collectors.toList()))
                .build();
    }
}
