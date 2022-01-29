package nextstep.subway.line.application.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.application.dto.StationResponse;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LineResponse {
    private final Long id;
    private final String name;
    private final String color;
    private final Integer length;
    private final List<StationResponse> stations;
    private final LocalDateTime createdDate;
    private final LocalDateTime modifiedDate;

    @Builder
    public LineResponse(Long id, String name, String color, int length, List<StationResponse> stations,
                        LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.length = length;
        this.stations = stations;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static LineResponse notWithStationsFrom(Line line) {
        return builder()
            .id(line.getId())
            .name(line.getName())
            .color(line.getColor())
            .createdDate(line.getCreatedDate())
            .modifiedDate(line.getModifiedDate())
            .build();
    }

    public static LineResponse withStationsFrom(Line line) {
        List<StationResponse> stations =
            line.getStations()
                .stream()
                .map(StationResponse::from)
                .collect(Collectors.toList());

        return builder()
            .id(line.getId())
            .name(line.getName())
            .color(line.getColor())
            .stations(stations)
            .length(line.getLength())
            .createdDate(line.getCreatedDate())
            .modifiedDate(line.getModifiedDate())
            .build();
    }
}

