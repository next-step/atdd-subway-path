package nextstep.subway.unit.test.utils;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.object.Distance;

import static nextstep.subway.unit.test.utils.Stations.서울역;
import static nextstep.subway.unit.test.utils.Stations.연신내;
import static nextstep.subway.unit.test.utils.Stations.판교역;

public class Lines {
    public static Line GTXA_연신내_서울역;
    public static Line GTXA_연신내_판교역;

    public static void 노선_초기화() {
        GTXA_연신내_서울역 = new Line("GTX-A", "bg-red-900", 연신내, 서울역, new Distance(10));
        GTXA_연신내_판교역 = new Line("GTX-A", "bg-red-900", 연신내, 판교역, new Distance(20));
    }
}
