package atdd.path.domain.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RouteResponseDto {
    private Long startStationId;
    private Long endStationId;
    private List<StationDto> stations;
}
