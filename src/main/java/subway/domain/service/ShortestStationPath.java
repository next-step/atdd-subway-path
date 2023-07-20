package subway.domain.service;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class ShortestStationPath {
    private List<Long> stationIds;
    private BigDecimal distance;
}
