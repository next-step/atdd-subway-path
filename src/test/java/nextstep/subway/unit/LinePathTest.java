package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LinePath;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 경로 도메인 객체 단위 테스트")
class LinePathTest {

    private Line 서울1호선 = new Line("1호선", "blue");
    private Line 서울2호선 = new Line("2호선", "green");
    private Line 서울9호선 = new Line("2호선", "yellow");
    private Station 당산역 = new Station("당산역");
    private Station 신도림역 = new Station("신도림역");
    private Station 노량진역 = new Station("노량진역");
    private Station 구로역 = new Station("구로역");

    private LinePath 지하철경로;

    // 당산 -- 5--- 노량진
    // |             | 10
    // |____ 20 ____신도림 ----10--- 구로

    // given
    @BeforeEach
    void setUp() {
        서울1호선.addSection(new Section(서울1호선, 노량진역, 신도림역, 10));
        서울1호선.addSection(new Section(서울1호선, 신도림역, 구로역, 10));
        서울2호선.addSection(new Section(서울2호선, 당산역, 신도림역, 20));
        서울9호선.addSection(new Section(서울9호선, 노량진역, 당산역, 5));
        지하철경로 = new LinePath(List.of(서울1호선, 서울2호선, 서울9호선));
    }

    @DisplayName("출발역과 도착역 사이의 최단경로 조회")
    @Test
    void getShortestDistance() {
        // when-then
        assertThat(지하철경로.getShortestDistance(당산역, 구로역)).isEqualTo(25);
        assertThat(지하철경로.getShortestDistance(당산역, 신도림역)).isEqualTo(15);
    }

}
