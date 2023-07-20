package subway.service.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class StationPathResponse {
    private BigDecimal distance;
    private List<StationResponse> stations;
}
