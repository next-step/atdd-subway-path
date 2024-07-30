package nextstep.subway.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class SectionResponse {
    private final Long subwayLineId;
    private final Long sectionId;
}
