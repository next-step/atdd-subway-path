package nextstep.subway.acceptance.test.utils;

import lombok.Builder;
import nextstep.subway.utils.BaseParams;

public class Paths {
    public static 최단거리_탐색_파람 최단거리_탐색;

    public static void 파람_초기화() {
        최단거리_탐색 = 최단거리_탐색_파람.builder()
                .source(1L)
                .target(3L)
                .build();
    }

    public static class 최단거리_탐색_파람 extends BaseParams {
        private final Long source;
        private final Long target;

        @Builder
        최단거리_탐색_파람(Long source, Long target) {
            this.source = source;
            this.target = target;
        }
    }
}
