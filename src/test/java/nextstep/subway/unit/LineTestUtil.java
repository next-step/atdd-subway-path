package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

/**
 * @author a1101466 on 2022/07/27
 * @project subway
 * @description
 */
public class LineTestUtil {
    static final Long 일호선ID    = 1L;
    static final String 일호선이름 = "일호선";
    static final String 라인색    = "blue";

    static final int DEFAULT_DISTANCE = 10;

    static Station 개봉역 = new Station(1L, "개봉역");
    static Station 구일역 = new Station(2L, "구일역");
    static Station 구로역 = new Station(2L, "구로역");


}
