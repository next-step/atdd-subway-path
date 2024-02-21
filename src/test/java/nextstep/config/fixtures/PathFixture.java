package nextstep.config.fixtures;

import nextstep.subway.dto.PathRequest;
import nextstep.subway.dto.SectionRequest;

public class PathFixture {

    public static PathRequest 지하철_경로(Long 출발역, Long 도착역) {
        return new PathRequest(출발역, 도착역);
    }
}
