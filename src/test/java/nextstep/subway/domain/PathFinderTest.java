package nextstep.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("경로 찾기 관련")
public class PathFinderTest {

    @DisplayName("지하철 경로 찾기")
    @Test
    void findPath() {
        // given
        PathFinder pathFinder = new PathFinder(List.of(Stub.이호선()));

        // when
        Path path = pathFinder.find(Stub.강남역, Stub.삼성역);

        // then
        assertThat(path.getStations()).containsExactly(Stub.강남역, Stub.역삼역, Stub.선릉역, Stub.삼성역);
        assertThat(path.getDistance()).isEqualTo(15);
    }

    @DisplayName("출발역과 도착역이 같은 경우 예외 발생")
    @Test
    void sameStations() {
        // given
        PathFinder pathFinder = new PathFinder(List.of(Stub.이호선()));

        // then
        assertThatThrownBy(() -> pathFinder.find(Stub.강남역, Stub.강남역))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("출발역과 도착역이 연결되지 않은 경우 예외 발생")
    @Test
    void isConnectStations() {
        // given
        PathFinder pathFinder = new PathFinder(List.of(Stub.일호선(), Stub.이호선()));

        // then
        assertThatThrownBy(() -> pathFinder.find(Stub.강남역, Stub.서울역))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private static class Stub {
        public static final Station 서울역 = new Station("서울역");
        public static final Station 시청역 = new Station("시청역");
        public static final Station 종각역 = new Station("종각역");
        public static final Station 강남역 = new Station("강남역");
        public static final Station 역삼역 = new Station("역삼역");
        public static final Station 선릉역 = new Station("선릉역");
        public static final Station 삼성역 = new Station("삼성역");

        public static Line 일호선() {
            Line line = new Line("1호선", "blue");
            line.addSection(Stub.서울역, Stub.시청역, 7);
            line.addSection(Stub.시청역, Stub.종각역, 4);
            return line;
        }

        public static Line 이호선() {
            Line line = new Line("2호선", "green");
            line.addSection(Stub.강남역, Stub.역삼역, 5);
            line.addSection(Stub.역삼역, Stub.선릉역, 4);
            line.addSection(Stub.선릉역, Stub.삼성역, 6);
            return line;
        }
    }
}
