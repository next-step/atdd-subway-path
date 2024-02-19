package nextstep.subway.unit;

import nextstep.subway.exception.SubwayException;
import nextstep.subway.line.Line;
import nextstep.subway.section.Section;
import nextstep.subway.station.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        Section nextSection = new Section(이호선, 역삼역, 선릉역, 10L);
        이호선.addSection(nextSection);

        Section middleSection = new Section(이호선, 역삼역, 삼성역, 3L);
        이호선.addSection(middleSection);

        assertThat(nextSection.getUpStation()).isEqualTo(삼성역);
        assertThat(이호선.getOrderedStations()).containsExactly(강남역, 역삼역, 삼성역, 선릉역);
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

    /**
     * Given 마지막 구간을 생성하고
     * When 마지막 역을 삭제하면
     * Then 노선 조회 시 등록한 역을 찾을 수 없다
     */
    @DisplayName("마지막 지하철역을 삭제하면 지하철역 목록 조회 시 생성한 역을 찾을 수 없다.")
    @Test
    void deleteEndSection() {
        Line 이호선 = new Line("2호선", "green", 강남역, 역삼역, 10L);
        Section section = new Section(이호선, 역삼역, 선릉역, 10L);
        이호선.addSection(section);

        이호선.removeSection(선릉역);

        assertThat(이호선.getOrderedStations()).containsExactly(강남역, 역삼역);
    }

    /**
     * Given 마지막 구간을 생성하고
     * When 가운데 역을 삭제하면
     * Then 노선 조회 시 등록한 역을 찾을 수 없다
     */
    @DisplayName("가운데 지하철역을 삭제하면 지하철역 목록 조회 시 생성한 역을 찾을 수 없다.")
    @Test
    void deleteMiddleSection() {
        Line 이호선 = new Line("2호선", "green", 강남역, 역삼역, 10L);
        Section 역삼_선릉 = new Section(이호선, 역삼역, 선릉역, 10L);
        이호선.addSection(역삼_선릉);

        Section 선릉_삼성 = new Section(이호선, 선릉역, 삼성역, 10L);
        이호선.addSection(선릉_삼성);

        이호선.removeSection(선릉역);

        assertThat(이호선.getOrderedStations()).containsExactly(강남역, 역삼역, 삼성역);
        assertThat(역삼_선릉.getDownStation()).isEqualTo(선릉_삼성.getDownStation());
        assertThat(역삼_선릉.getDistance()).isEqualTo(20L);
    }

    /**
     * Given 마지막 구간을 생성하고
     * When 상행 종점 역을 삭제하면
     * Then 노선 조회 시 등록한 역을 찾을 수 없다
     */
    @DisplayName("상행 종점 역을 삭제하면 지하철역 목록 조회 시 생성한 역을 찾을 수 없다.")
    @Test
    void deleteStartSection() {
        Line 이호선 = new Line("2호선", "green", 강남역, 역삼역, 10L);
        Section section = new Section(이호선, 역삼역, 선릉역, 10L);
        이호선.addSection(section);

        이호선.removeSection(강남역);

        assertThat(이호선.getOrderedStations()).containsExactly(역삼역, 선릉역);
    }

    /**
     * When 구간이 1개인 경우 삭제하면
     * Then 에러를 반환한다.
     */
    @DisplayName("구간이 1개인 경우 삭제하면 에러를 반환한다.")
    @Test
    void validateLastSection() {
        Line 이호선 = new Line("2호선", "green", 강남역, 역삼역, 10L);

        assertThrows(SubwayException.class, () -> 이호선.removeSection(강남역));
    }

    /**
     * Given 구간을 생성하고
     * When 존재하지 않는 역을 삭제하면
     * Then 에러를 반환한다.
     */
    @DisplayName("존재하지 않는 역을 삭제하면 에러를 반환한다.")
    @Test
    void validatePresent() {
        Line 이호선 = new Line("2호선", "green", 강남역, 역삼역, 10L);
        Section section = new Section(이호선, 역삼역, 선릉역, 10L);
        이호선.addSection(section);

        assertThrows(SubwayException.class, () -> 이호선.removeSection(삼성역));
    }
}
