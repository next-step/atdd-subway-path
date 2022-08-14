package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.utils.TestObjectFactory;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class PathTest {

    private final TestObjectFactory testObjectFactory = new TestObjectFactory();

    /**
     *        /신분당선/
     *          |
     * /구호선/ - 강남역 - 신논현역 - 고속터미널역
     *           |
     *          양재역
     */

    @Test
    void getShortestPath() {
        //given
        Line 분당선 = testObjectFactory.노선생성("분당선");
        Station 강남역 = testObjectFactory.역생성("강남역");
        Station 양재역 = testObjectFactory.역생성("양재역");
        Station 신논현역 = testObjectFactory.역생성("신논현역");

        Line 구호선 = testObjectFactory.노선생성("구호선");
        Station 고속터미널 = testObjectFactory.역생성("고속터미널");

        Section 강남_양재 = testObjectFactory.구간생성(분당선, 강남역, 양재역, 1000);
        Section 신논현_강남 = testObjectFactory.구간생성(분당선, 신논현역, 강남역, 10);
        Section 고터_신논현 = testObjectFactory.구간생성(구호선, 고속터미널, 신논현역, 10);

        Path path = new Path(Set.of(강남역, 양재역, 신논현역, 고속터미널), List.of(강남_양재, 신논현_강남, 고터_신논현));

        //when
        List<Station> shortestPath = path.getShortestPath(양재역, 고속터미널);
        //then
        assertThat(shortestPath).containsExactly(양재역, 강남역, 신논현역, 고속터미널);
    }

    @Test
    void getShortestWeight() {
        //given
        Line 분당선 = testObjectFactory.노선생성("분당선");
        Station 강남역 = testObjectFactory.역생성("강남역");
        Station 양재역 = testObjectFactory.역생성("양재역");
        Station 신논현역 = testObjectFactory.역생성("신논현역");

        Line 구호선 = testObjectFactory.노선생성("구호선");
        Station 고속터미널 = testObjectFactory.역생성("고속터미널");

        Section 강남_양재 = testObjectFactory.구간생성(분당선, 강남역, 양재역, 1000);
        Section 신논현_강남 = testObjectFactory.구간생성(분당선, 신논현역, 강남역, 10);
        Section 고터_신논현 = testObjectFactory.구간생성(구호선, 고속터미널, 신논현역, 10);

        Path path = new Path(Set.of(강남역, 양재역, 신논현역, 고속터미널), List.of(강남_양재, 신논현_강남, 고터_신논현));

        //when
        int shortestWeight = path.getShortestWeight(양재역, 고속터미널);

        //then
        assertThat(shortestWeight).isEqualTo(강남_양재.getDistance() + 신논현_강남.getDistance() + 고터_신논현.getDistance());
    }
}
