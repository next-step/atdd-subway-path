package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;

class LineTest {


    /**
     * GIVEN 지하철 노선이 생성면 구간이 1개이고
     * WHEN 구간이 추가되면
     * THEN 구간의 수는 2개가 된다.
     */
    @Test
    void addSection() {
        // given
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 선릉역 = new Station("선릉역");

        Line line = new Line("2호선", "green", 강남역, 역삼역, 10);

        // when
        line.addSection(역삼역, 선릉역, 10);

        // then
        assertThat(line.getSections()).hasSize(2);
    }

    /**
     * WHEN 노선을 생성했을 때
     * THEN 노선에 등록된 역은 2개다.
     */
    @Test
    void getStations() {
        // when
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");

        Line line = new Line("2호선", "green", 강남역, 역삼역, 10);

        // then
        assertThat(line.getStations()).hasSize(2);
    }

    /**
     * GIVEN 노선에 구간이 2개일 때
     * WHEN 노선에서 구간을 삭제하면
     * THEN 노선의 구간은 1개이다.
     */
    @Test
    void removeSection() {
        // given
        Station 강남역 = new Station("강남역");
        Station 역삼역 = new Station("역삼역");
        Station 선릉역 = new Station("선릉역");

        Line line = new Line("2호선", "green", 강남역, 역삼역, 10);
        line.addSection(역삼역, 선릉역, 10);

        // when
        line.removeSection(역삼역, 선릉역);

        // then
        assertThat(line.getSections()).hasSize(1);
    }
}
