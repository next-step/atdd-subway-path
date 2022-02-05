package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {
    private final Line line = new Line("2호선", "green");
    private final Station 강남역 = new Station("강남역");
    private final Station 잠실역 = new Station("잠실역");
    private final int distance = 10;

    @DisplayName("구간 목록 마지막에 새로운 구간을 추가할 경우")
    @Test
    void addSection() {
        // when
        line.addSection(강남역, 잠실역, distance);

        // then
        assertThat(line.getSections().get(0).getUpStation()).isEqualTo(강남역);
        assertThat(line.getSections().get(0).getDownStation()).isEqualTo(잠실역);
        assertThat(line.getSections().get(0).getDistance()).isEqualTo(distance);
    }

    @DisplayName("노선에 속해있는 역 목록 조회")
    @Test
    void getStations() {
        // given
        line.addSection(강남역, 잠실역, distance);

        // when
        List<Station> stations = line.getStations();

        // then
        assertThat(stations).containsExactly(강남역, 잠실역);
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class update_메서드는 {
        private final String originalName = "2호선";
        private final String originalColor = "green";
        private final Line line = new Line(originalName, originalColor);

        private final String name = "신분당선";
        private final String color = "red";

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 만약_이름만_있고_색깔은_null이라면 {

            @Test
            @DisplayName("이름만 변경된다")
            void 이름만_변경된다() {
                line.update(name, null);

                assertThat(line.getName()).isEqualTo(name);
                assertThat(line.getColor()).isEqualTo(originalColor);
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 만약_이름은_null이고_색깔만_있다면 {

            @Test
            @DisplayName("색깔만 변경된다")
            void 색깔만_변경된다() {
                line.update(null, color);

                assertThat(line.getName()).isEqualTo(originalName);
                assertThat(line.getColor()).isEqualTo(color);
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 만약_이름과_색깔_모두_있다면 {

            @Test
            @DisplayName("이름과 색깔 모두 변경된다")
            void 이름과_색깔_모두_변경된다() {
                line.update(name, color);

                assertThat(line.getName()).isEqualTo(name);
                assertThat(line.getColor()).isEqualTo(color);
            }
        }
    }
}
