package nextstep.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("경로 찾기 관련")
public class PathFinderTest {

    @DisplayName("지하철 경로 찾기")
    @Test
    void findPath() {
        // given
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 선릉역 = new Station("선릉역");
        Station 삼성역 = new Station("삼성역");

        Line line = new Line();
        line.addSection(강남역, 역삼역, 5);
        line.addSection(역삼역, 선릉역, 4);
        line.addSection(선릉역, 삼성역, 6);

        // when
        PathFinder pathFinder = new PathFinder(강남역, 삼성역);
        Path path = pathFinder.find(line);

        // then
        assertThat(path.getStations()).containsExactly(강남역, 역삼역, 선릉역, 삼성역);
        assertThat(path.getDistance()).isEqualTo(15);
    }
}
