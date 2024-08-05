package nextstep.subway.line.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SectionRequest {

    private Long upStationId;
    private Long downStationId;
    private Long distance;
}
