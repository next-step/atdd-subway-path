package subway.line.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SectionDeleteRequest {
    private Long stationId;
    private Long lineId;
}
