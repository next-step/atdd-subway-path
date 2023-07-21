package nextstep.subway.line.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.station.dto.StationResponse;

import java.util.List;

@Getter
@NoArgsConstructor
public class ShortestPathResponse {

    private List<StationResponse> stations;
    private Integer distance;
}
