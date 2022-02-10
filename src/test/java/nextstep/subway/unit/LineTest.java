package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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


    @DisplayName("맨 앞에 새로운 구간을 추가")
    @Test
    void addSectionInTheFront() {
        // given
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 선릉역 = new Station("선릉역");
        Line line = Line.of("2호선", "bg-green-600", 강남역, 역삼역, 10);

        // when
        line.addSection(선릉역, 강남역, 10);

        // then
        assertThat(line.getStations()).containsExactly(선릉역, 강남역, 역삼역);
    }


    @DisplayName("맨 마지막에 새로운 구간을 추가")
    @Test
    void addSectionAtTheEnd() {
        // given
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 선릉역 = new Station("선릉역");
        Line line = Line.of("2호선", "bg-green-600", 강남역, 역삼역, 10);

        // when
        line.addSection(역삼역, 선릉역, 10);

        // then
        assertThat(line.getStations()).containsExactly(강남역, 역삼역, 선릉역);
    }

    @DisplayName("기존 구간에 새로운 구간을 추가")
    @Test
    void addSectionInTheMiddle() {
        // given
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 선릉역 = new Station("선릉역");
        Line line = Line.of("2호선", "bg-green-600", 강남역, 역삼역, 10);

        // when
        line.addSection(강남역, 선릉역, 7);

        // then
        assertThat(line.getStations()).containsExactly(강남역, 선릉역, 역삼역);
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않는 경우")
    @Test
    void addSectionExceptionDoesNotExistStations() {
        // given
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Line line = Line.of("2호선", "bg-green-600", 강남역, 역삼역, 10);

        Station 교대역 = new Station("교대역");
        Station 서초역 = new Station("서초역");

        // when
        // then
        assertThrows(IllegalArgumentException.class, () -> line.addSection(교대역, 서초역, 10));
    }

    @DisplayName("상행역과 하행역 둘 다 포함되어있는 경우")
    @Test
    void addSectionExceptionDuplicatedSection() {
        // given
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Line line = Line.of("2호선", "bg-green-600", 강남역, 역삼역, 10);

        // when
        // then
        assertThrows(IllegalArgumentException.class, () -> line.addSection(강남역, 역삼역, 7));
    }

    @DisplayName("역 사이에 새로운 역을 추가 할 때, 기존 역사이보다 크거나 같은 경우")
    @Test
    void addSectionExceptionLongerThanBefore() {
        // given
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 선릉역 = new Station("선릉역");
        Line line = Line.of("2호선", "bg-green-600", 강남역, 역삼역, 10);

        // when
        // then
        assertThrows(IllegalArgumentException.class, () -> line.addSection(강남역, 선릉역, 11));
    }
}
