package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import javax.persistence.EntityNotFoundException;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
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

    /**
     * Given 1개의 구간을 가진 지하철 노선이 등록되어 있다.
     * When 가운데에 역을 추가한다.
     * Then 다시 조회한 노선의 역은 3개이다.
     * Then 구간은 2개다.
     * Then 역의 거리는 나뉘어져 있다.
     */
    @Test
    void 노선에_역_추가시_노선_가운데_추가할_수_있다() {
        // given
        final Station 강남역 = new Station("강남역");
        final Station 역삼역 = new Station("역삼역");
        final Station 선릉역 = new Station("선릉역");

        final Line line = new Line("2호선", "green", 강남역, 역삼역, 10);

        // when
        line.addSection(강남역, 선릉역, 3);

        // then
        assertThat(line.getStations()).hasSize(3);

        // then
        assertThat(line.getSections()).hasSize(2);

        // then
        final Section addedSection = line.getSections().stream().filter(section -> section.getUpStation() == 강남역).findFirst().orElseThrow(
            EntityNotFoundException::new);
        final Section splitedSection = line.getSections().stream().filter(section -> section.getUpStation() == 선릉역).findFirst().orElseThrow(
            EntityNotFoundException::new
        );
        assertThat(addedSection.getDistance()).isEqualTo(3);
        assertThat(splitedSection.getDistance()).isEqualTo(7);
    }

    /**
     * Given 1개의 구간을 가진 지하철 노선이 등록되어 있다.
     * When 같은 노선을 추가한다.
     * Then 실패한다.
     */
    @Test
    void 노선에_역_추가시_노선에_같은_역_추가시_실패한다() {
        // given
        final Station 강남역 = new Station("강남역");
        final Station 역삼역 = new Station("역삼역");

        final Line line = new Line("2호선", "green", 강남역, 역삼역, 10);

        // when
        // then
        assertThatThrownBy(() -> line.addSection(강남역, 역삼역, 3))
            .isInstanceOf(IllegalArgumentException.class);
    }
    /**
     * Given 1개의 구간을 가진 지하철 노선이 등록되어 있다.
     * When 같은 노선을 추가한다.
     * Then 실패한다.
     */
    @Test
    void 노선에_역_추가시_둘다_기존_노선에_포함되지_않는_경우_실패한다() {
        // given
        final Station 강남역 = new Station("강남역");
        final Station 역삼역 = new Station("역삼역");
        final Station 선릉역 = new Station("선릉역");
        final Station 삼성역 = new Station("삼성역");

        final Line line = new Line("2호선", "green", 강남역, 역삼역, 10);

        // when
        // then
        assertThatThrownBy(() -> line.addSection(선릉역, 삼성역, 3))
            .isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Given 3개의 역을 가진 노선이 주어진다.
     * When 상행선을 삭제한다.
     * Then Section이 1개가 된다.
     * Then Station은 2개가 된다.
     * Then Stations 응답에 삭제한 역은 존재하지 않는다.
     */
    @Test
    void 노선에서_상행선을_삭제할_수_있다() {
        // given
        final Station 강남역 = new Station("강남역");
        final Station 역삼역 = new Station("역삼역");
        final Station 선릉역 = new Station("선릉역");

        final Line line = new Line("2호선", "green", 강남역, 역삼역, 10);
        line.addSection(역삼역, 선릉역, 10);

        // when
        line.removeStation(강남역);

        // then
        assertThat(line.getSections()).hasSize(1);

        // then
        final List<Station> stations = line.getStations();
        assertThat(stations).hasSize(2);
        assertThat(stations).doesNotContain(강남역);
    }

    /**
     * Given 4개의 역을 가진 노선이 주어진다.
     * When 가운데 있는 역을 삭제한다.
     * Then Section은 2개가 된다.
     * Then Station은 3개가 된다.
     * Then Stations 응답에 삭제한 역은 존재하지 않는다.
     */
    @Test
    void 노선에서_가운데_있는_역을_삭제_할_수_있다() {
        // given
        final Station 강남역 = new Station("강남역");
        final Station 역삼역 = new Station("역삼역");
        final Station 선릉역 = new Station("선릉역");
        final Station 삼성역 = new Station("삼성역");

        final Line line = new Line("2호선", "green", 강남역, 역삼역, 10);
        line.addSection(역삼역, 선릉역, 10);
        line.addSection(선릉역, 삼성역, 10);

        // when
        line.removeStation(역삼역);

        // then
        assertThat(line.getSections()).hasSize(2);

        // then
        final List<Station> stations = line.getStations();
        assertThat(stations).hasSize(3);
        assertThat(stations).doesNotContain(역삼역);
    }

    /**
     * Given 4개의 역을 가진 노선이 주어진다.
     * When 마지막에 있는 역을 삭제한다.
     * Then Section은 2개가 된다.
     * Then Station은 3개가 된다.
     * Then Stations 응답에 삭제한 역은 존재하지 않는다.
     */
    @Test
    void 노선에서_마지막에_있는_역을_삭제_할_수_있다() {
        // given
        final Station 강남역 = new Station("강남역");
        final Station 역삼역 = new Station("역삼역");
        final Station 선릉역 = new Station("선릉역");
        final Station 삼성역 = new Station("삼성역");

        final Line line = new Line("2호선", "green", 강남역, 역삼역, 10);
        line.addSection(역삼역, 선릉역, 10);
        line.addSection(선릉역, 삼성역, 10);

        // when
        line.removeStation(삼성역);

        // then
        assertThat(line.getSections()).hasSize(2);

        // then
        final List<Station> stations = line.getStations();
        assertThat(stations).hasSize(3);
        assertThat(stations).doesNotContain(삼성역);
    }
}
