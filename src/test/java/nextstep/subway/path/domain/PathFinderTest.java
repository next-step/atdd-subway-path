package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DisplayName("경로 찾기 테스트")
class PathFinderTest {

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

    // Happy case
    @DisplayName("최단경로 조회")
    @Test
    void getShortestPath(){
        // given
        // 지하철 역이 등록됨
        Station 선정릉역 = mock(Station.class);
        ReflectionTestUtils.setField(선정릉역, "id", 1L);
        Station 선릉역 = mock(Station.class);
        ReflectionTestUtils.setField(선릉역, "id", 2L);
        Station 봉은사역 = mock(Station.class);
        ReflectionTestUtils.setField(봉은사역, "id", 3L);
        Station 종합운동장역 = mock(Station.class);
        ReflectionTestUtils.setField(종합운동장역, "id", 4L);
        Station 삼성역 = mock(Station.class);
        ReflectionTestUtils.setField(삼성역, "id", 5L);

        // 지하철 노선이 등록됨
        Line 이호선 = new Line("2호선", "bg-green-600", 선릉역, 종합운동장역, 5);
        Line 구호선 = new Line("9호선", "bg-brown-600", 선정릉역, 종합운동장역, 9);
        Line 분당선 = new Line("분당선", "bg-yellow-600", 선정릉역, 선릉역, 20);

        // 지하철 역에 노선들이 등록됨
        이호선.addSection(선릉역, 삼성역, 3);
        구호선.addSection(선정릉역, 봉은사역, 4);

        // when
        // 최단 경로를 조회함
        PathFinder pathFinder = new PathFinder(Arrays.asList(이호선, 구호선, 분당선));
        PathResponse response = pathFinder.getShortestPath(선정릉역.getId(), 삼성역.getId());

        // then
        // 최단 경로 조회됨
        assertThat(response.getDistance()).isEqualTo(11);
        assertThat(response.getStations()).extracting("id")
                        .containsExactlyElementsOf(Arrays.asList(선정릉역.getId(), 봉은사역.getId(), 종합운동장역.getId(), 삼성역.getId()));
    }

}