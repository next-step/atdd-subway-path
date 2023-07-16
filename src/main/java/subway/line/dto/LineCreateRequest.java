package subway.line.dto;

import lombok.Builder;
import lombok.Getter;
import subway.line.model.Line;
import subway.station.model.Station;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Getter
@Builder
public class LineCreateRequest {

    private final String DISTANCE_MIN_MESSAGE = "구간의 최소 거리는 1 이상이어야 합니다.";

    @NotBlank
    private String name;

    @NotBlank
    private String color;

    @NotBlank
    private Long upStationId;

    @NotBlank
    private Long downStationId;

    @NotBlank
    @Min(value = 1L, message = DISTANCE_MIN_MESSAGE)
    private Long distance;

    public static Line to(LineCreateRequest request,
                          Station upStation,
                          Station downStation) {
        return Line.builder()
                .name(request.getName())
                .color(request.getColor())
                .build();
    }
}
