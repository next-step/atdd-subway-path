package nextstep.subway.line.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.station.dto.StationResponse;

import java.math.BigInteger;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShortestPathResponse {

    private List<StationResponse> stations;
    private BigInteger distance;
}
