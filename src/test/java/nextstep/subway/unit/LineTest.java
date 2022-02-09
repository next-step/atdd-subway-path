package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {
    @DisplayName("구간 목록 마지막에 새로운 구간을 추가할 경우")
    @Test
    void addSection() {
        // given
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 선릉역 = new Station("선릉역");
        Line line = Line.of("2호선", "bg-green-600", 강남역, 역삼역, 10);

        // when
        line.addSection(역삼역, 선릉역, 5);

        // then
        assertThat(line.getStations()).containsExactly(강남역, 역삼역, 선릉역);
    }

    @DisplayName("노선에 속해있는 역 목록 조회")
    @Test
    void getStations() {
        // given
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Line line = Line.of("2호선", "bg-green-600", 강남역, 역삼역, 10);

        // when
        List<Station> stations = line.getStations();

        // then
        assertThat(stations).containsExactly(강남역, 역삼역);
    }

    @DisplayName("구간이 목록에서 마지막 역 삭제")
    @Test
    void removeSection() {
        // given
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 선릉역 = new Station("선릉역");
        Line line = Line.of("2호선", "bg-green-600", 강남역, 역삼역, 10);
        line.addSection(역삼역, 선릉역, 5);

        // when
        line.deleteSection(선릉역);

        // then
        assertThat(line.getStations()).doesNotContain(선릉역);
    }
}
