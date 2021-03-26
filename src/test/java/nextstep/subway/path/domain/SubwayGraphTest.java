package nextstep.subway.path.domain;

import com.google.common.collect.Lists;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SubwayGraphTest {
    @Test
    void findPath() {
        // given
        Station 강남역 = new Station(1L, "강남역");
        Station 역삼역 = new Station(1L, "역삼역");
        Station 선릉역 = new Station(1L, "선릉역");
        Line line = new Line(1L, "2호선", "green");
        line.addSection(강남역, 역삼역, 10, 10);
        line.addSection(역삼역, 선릉역, 10, 10);
        SubwayGraph subwayGraph = new SubwayGraph(Lists.newArrayList(line), PathType.DISTANCE);

        // when
        PathResult pathResult = subwayGraph.findPath(선릉역, 강남역);

        // then
        assertThat(pathResult.getStations()).containsExactly(선릉역, 역삼역, 강남역);
    }
}
