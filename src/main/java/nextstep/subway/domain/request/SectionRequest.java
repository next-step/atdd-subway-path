package nextstep.subway.domain.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SectionRequest {
    private Long upStationId;

    private Long downStationId;

    private int distance;


}
