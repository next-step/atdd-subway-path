package nextstep.subway.domain.factory;

import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.SectionRequest;

public class DtoFactory {
    public static LineRequest createLineRequest(String name, String color, Long upStation, Long downStation, int distance) {
        return new LineRequest(name, color, upStation, downStation, distance);
    }

    public static SectionRequest createSectionRequest(Long upStation, Long downStation, int distance) {
        return new SectionRequest(upStation, downStation, distance);
    }
}
