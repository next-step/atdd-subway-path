package subway.line.dto;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Getter
@Builder
public class SectionCreateRequest {
    private final String DOWN_STATION_ID_NOT_BLANK_MESSAGE = "하행역은 반드시 지정해야 합니다.";
    private final String UP_STATION_ID_NOT_BLANK_MESSAGE = "상행역은 반드시 지정해야 합니다.";
    private final String DISTANCE_NOT_BLANK_MESSAGE = "구간의 거리는 반드시 지정해야 합니다.";
    private final String DISTANCE_MIN_MESSAGE = "구간의 최소 거리는 1 이상이어야 합니다.";

    @NotBlank(message = DOWN_STATION_ID_NOT_BLANK_MESSAGE)
    private Long downStationId;

    @NotBlank(message = UP_STATION_ID_NOT_BLANK_MESSAGE)
    private Long upStationId;

    @NotBlank(message = DISTANCE_NOT_BLANK_MESSAGE)
    @Min(value = 1, message = DISTANCE_MIN_MESSAGE)
    private Long distance;
}
