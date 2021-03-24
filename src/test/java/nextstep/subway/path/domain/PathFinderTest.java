package nextstep.subway.path.domain;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.domain.Line;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;

import static nextstep.subway.path.acceptance.steps.PathSteps.최단거리_조회_실패됨;
import static nextstep.subway.path.acceptance.steps.PathSteps.최단거리_조회_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("경로 찾기 테스트")
class PathFinderTest {

    private Station 선정릉역, 선릉역, 봉은사역, 종합운동장역, 삼성역;
    private Line 이호선, 구호선, 분당선;
    private PathFinder pathFinder;

    /**
     * <노선도>
     *      선정릉역 --(20)-- 선릉역
     *   (4) /               /
     *     봉은사역           / (3)
     *  (5) /             /
     *   종합운동장역 -(2)- 삼성역
     *
     * <최단거리>
     * 선정릉역 -> 삼성역 : 선정릉역 -> 봉은사역 -> 종합운동장역 -> 삼성역 (11)
     */
    @BeforeEach
    public void setUp() {
        // 지하철 역이 등록됨
        선정릉역 = new Station("선정릉역");
        ReflectionTestUtils.setField(선정릉역, "id", 1L);
        선릉역 = new Station("선릉역");
        ReflectionTestUtils.setField(선릉역, "id", 2L);
        봉은사역 = new Station("봉은사역");
        ReflectionTestUtils.setField(봉은사역, "id", 3L);
        종합운동장역 = new Station("종합운동장역");
        ReflectionTestUtils.setField(종합운동장역, "id", 4L);
        삼성역 = new Station("삼성역");
        ReflectionTestUtils.setField(삼성역, "id", 5L);

        // 지하철 노선이 등록됨
        이호선 = new Line("2호선", "bg-green-600", 선릉역, 종합운동장역, 5);
        ReflectionTestUtils.setField(이호선, "id", 1L);
        구호선 = new Line("9호선", "bg-brown-600", 선정릉역, 종합운동장역, 9);
        ReflectionTestUtils.setField(구호선, "id", 2L);
        분당선 = new Line("분당선", "bg-yellow-600", 선정릉역, 선릉역, 20);
        ReflectionTestUtils.setField(분당선, "id", 3L);

        // 지하철 역에 노선들이 등록됨
        이호선.addSection(선릉역, 삼성역, 3);
        구호선.addSection(선정릉역, 봉은사역, 4);

        pathFinder = new PathFinder(Arrays.asList(이호선, 구호선, 분당선));
    }

    // Happy case

    @DisplayName("최단경로 조회")
    @Test
    void getShortestPath(){
        // when 최단 경로를 조회함
        PathResponse response = pathFinder.getShortestPath(선정릉역, 삼성역);

        // then 최단 경로 조회됨
        assertThat(response.getDistance()).isEqualTo(11);
        assertThat(response.getStations()).extracting("id")
                        .containsExactlyElementsOf(Arrays.asList(선정릉역.getId(), 봉은사역.getId(), 종합운동장역.getId(), 삼성역.getId()));
    }

    // 예외케이스

    @DisplayName("[예외처리] 출발역과 도착역이 같은 경우")
    @Test
    void sameSourceAndTarget(){
        // when + then
        assertThatThrownBy(() -> {
            pathFinder.getShortestPath(선정릉역, 선정릉역);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("[예외처리] 출발역과 도착역이 연결이 되어 있지 않은 경우")
    @Test
    void unconnectedSourceAndTarget(){
        // given
        Station 미개통역 = new Station("미개통역");
        ReflectionTestUtils.setField(미개통역, "id", 100L);

        // when + then
        assertThatThrownBy(() -> {
            pathFinder.getShortestPath(선정릉역, 미개통역);
        }).isInstanceOf(IllegalArgumentException.class);
    }

}