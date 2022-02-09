package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

import java.util.List;

import static java.util.Arrays.asList;
import static nextstep.subway.unit.LineFixtures.*;

abstract class ShortestPathTestableLinesFixture {

    /**
     * 교대역(1)    --- *2호선(700 m)* ---    강남역(2)    ---*2호선(200 m) ---    역삼역(3)
     * |                                         |                                   |
     * *3호선(300 m)*                      *신분당선(2200 m)*                   4호선(800 m)
     * |                                        |                                   |
     * 남부터미널역(4) --- *3호선(300 m)* --- 양재(5)  --- *3호선(100 m)* ---    판교(6)
     */

    protected Station 교대역 = 역_생성(1L);
    protected Station 강남역 = 역_생성(2L);
    protected Station 역삼역 = 역_생성(3L);
    protected Station 남부터미널역 = 역_생성(4L);
    protected Station 양재역 = 역_생성(5L);
    protected Station 판교역 = 역_생성(6L);

    protected Section 교대역과_강남역_구간 = 구간_생성(1L, 교대역, 강남역, 700);
    protected Section 강남역과_역삼역_구간 = 구간_생성(2L, 강남역, 역삼역, 200);
    protected Section 교대역과_남부터미널역_구간 = 구간_생성(3L, 교대역, 남부터미널역, 300);

    protected Section 남부터미널역과_양재역_구간 = 구간_생성(4L, 남부터미널역, 양재역, 300);
    protected Section 양재역과_판교역_구간 = 구간_생성(4L, 양재역, 판교역, 100);

    protected Section 강남역과_양재역_구간 = 구간_생성(5L, 강남역, 양재역, 2200);
    protected Section 역삼역과_판교역_구간 = 구간_생성(6L, 역삼역, 판교역, 800);

    protected Line 노선_2호선 = 상행종점역부터_하행종점역까지_모든구간이_포함된_노선_생성("2호선",
            교대역과_강남역_구간,
            강남역과_역삼역_구간);
    protected Line 노선_3호선 = 상행종점역부터_하행종점역까지_모든구간이_포함된_노선_생성("3호선",
            교대역과_남부터미널역_구간,
            남부터미널역과_양재역_구간,
            양재역과_판교역_구간);
    protected Line 노선_신분당선 = 상행종점역부터_하행종점역까지_모든구간이_포함된_노선_생성("신분당선",
            강남역과_양재역_구간);
    protected Line 노선_4호선 = 상행종점역부터_하행종점역까지_모든구간이_포함된_노선_생성("4호선",
            역삼역과_판교역_구간);

    protected List<Line> lines = asList(노선_2호선, 노선_3호선, 노선_신분당선, 노선_4호선);
}
