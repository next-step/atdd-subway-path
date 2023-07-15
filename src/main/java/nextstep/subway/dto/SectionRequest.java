package nextstep.subway.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class SectionRequest {

    private Long upStationId;
    private Long downStationId;
    private int distance;

}
