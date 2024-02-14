package nextstep.subway.unit;

import nextstep.subway.line.Line;
import nextstep.subway.section.Section;
import nextstep.subway.station.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    Station 강남역;
    Station 역삼역;
    Station 선릉역;
    Station 삼성역;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        선릉역 = new Station("선릉역");
        삼성역 = new Station("삼성역");
    }

    @DisplayName("지하철 노선 마지막에 구간을 등록한다.")
    @Test
    void addEndSection() {
        Line 이호선 = new Line("2호선", "green", 강남역, 역삼역, 10L);

        Section section = new Section(이호선, 역삼역, 선릉역, 10L);
        이호선.addSection(section);

        assertThat(이호선.getOrderedStations()).containsExactly(강남역, 역삼역, 선릉역);
    }

    @DisplayName("지하철 노선 가운데에 구간을 등록한다.")
    @Test
    void addMiddleSection() {
        Line 이호선 = new Line("2호선", "green", 강남역, 역삼역, 10L);

        Section section = new Section(이호선, 강남역, 선릉역, 3L);
        이호선.addSection(section);

        assertThat(이호선.getOrderedStations()).containsExactly(강남역, 선릉역, 역삼역);
    }

    @DisplayName("지하철 노선 가운데에 구간 등록 시 다음 구간의 거리를 업데이트 한다.")
    @Test
    void updateNextSectionDistance() {
        Line 이호선 = new Line("2호선", "green", 강남역, 역삼역, 10L);
        Section nextSection = new Section(이호선, 역삼역, 선릉역, 10L);
        이호선.addSection(nextSection);

        Section middleSection = new Section(이호선, 역삼역, 삼성역, 3L);
        이호선.addSection(middleSection);

        assertThat(nextSection.getDistance()).isEqualTo(7L);
    }

    @DisplayName("지하철 노선의 지하철역 목록을 조회한다.")
    @Test
    void getStations() {
        Line 이호선 = new Line("2호선", "green", 강남역, 역삼역, 10L);

        assertThat(이호선.getOrderedStations()).containsExactly(강남역, 역삼역);
    }

    @DisplayName("지하철 노선에 구간을 삭제한다.")
    @Test
    void removeSection() {
        Line 이호선 = new Line("2호선", "green", 강남역, 역삼역, 10L);
        Section section = new Section(이호선, 역삼역, 선릉역, 10L);
        이호선.addSection(section);

        이호선.removeSection(선릉역);

        assertThat(이호선.getOrderedStations()).containsExactly(강남역, 역삼역);
    }
}
