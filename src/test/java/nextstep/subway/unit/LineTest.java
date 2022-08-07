package nextstep.subway.unit;

import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.section.Section;
import nextstep.subway.domain.section.Sections;
import nextstep.subway.domain.station.Station;
import nextstep.subway.domain.station.Stations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    private Station 강남역;
    private Station 역삼역;
    private Station 삼성역;
    private Line 이호선;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        삼성역 = new Station("삼성역");
        이호선 = new Line("2호선", "green");
    }

    @DisplayName("노선에 연결되어있는 정렬된 지하철 노선 목록 조회")
    @Test
    void getStations() {
        // given
        이호선.addSection(new Section(이호선, 강남역, 삼성역, 10));
        이호선.addSection(new Section(이호선, 강남역, 역삼역, 9));

        // when
        final Stations 이호선_역_목록 = 이호선.getStations();

        // then
        assertThat(이호선_역_목록.getList()).containsExactly(강남역, 역삼역, 삼성역);
    }

    @DisplayName("지하철 노선 수정")
    @Test
    void update() {
        // when
        이호선.update("신분당선", "red");
        // then
        assertThat(이호선.getName()).isEqualTo("신분당선");
        assertThat(이호선.getColor()).isEqualTo("red");
    }

    @DisplayName("노선의 하행 종점역으로 새로운 구간 추가")
    @Test
    void addSectionWithDownStation() {
        // Given
        final Section 강남역_역삼역_구간 = new Section(이호선, 강남역, 역삼역, 10);
        이호선.addSection(강남역_역삼역_구간);

        // When
        final Section 역삼역_삼성역_구간 = new Section(이호선, 역삼역, 삼성역, 10);
        이호선.addSection(역삼역_삼성역_구간);

        // Then
        final Sections 이호선_구간_리스트 = 이호선.getSections();
        assertThat(이호선_구간_리스트.getList()).hasSize(2);
    }

    @DisplayName("노선의 역과 역 사이에 새로운 구간 추가")
    @Test
    void addSectionBetweenStations() {
        // given
        final Section 강남역_역삼역_구간 = new Section(이호선, 강남역, 역삼역, 10);
        이호선.addSection(강남역_역삼역_구간);

        // when
        final Section 강남역_삼성역_구간 = new Section(이호선, 강남역, 삼성역, 10);
        이호선.addSection(강남역_삼성역_구간);

        // then
        assertThat(이호선.getSections().getList()).hasSize(2);
        assertThat(이호선.getStations().getList()).containsExactly(강남역, 삼성역, 역삼역);
    }

    @DisplayName("노선의 상행 종점역으로 새로운 구간 추가")
    @Test
    void addSectionWithUpStation() {
        // given
        final Section 강남역_역삼역_구간 = new Section(이호선, 강남역, 역삼역, 10);
        이호선.addSection(강남역_역삼역_구간);

        // when
        final Section 삼성역_강남역_구간 = new Section(이호선, 삼성역, 강남역, 10);
        이호선.addSection(삼성역_강남역_구간);

        // then
        assertThat(이호선.getSections().getList()).hasSize(2);
        assertThat(이호선.getStations().getList()).containsExactly(삼성역, 강남역, 역삼역);
    }

    @DisplayName("구간 목록 가져오기")
    @Test
    void getSections() {
        // Given
        final Section 강남역_역삼역_구간 = new Section(이호선, 강남역, 역삼역, 10);
        이호선.addSection(강남역_역삼역_구간);

        // When
        final Sections 이호선_구간_리스트 = 이호선.getSections();

        // Then
        assertThat(이호선_구간_리스트.getList()).hasSize(1);
    }

    @DisplayName("마지막 구간 가져오기")
    @Test
    void getLastSection() {
        // given
        final Section 강남역_역삼역_구간 = new Section(이호선, 강남역, 역삼역, 10);
        이호선.addSection(강남역_역삼역_구간);
        final Section 역삼역_삼성역_구간 = new Section(이호선, 역삼역, 삼성역, 10);
        이호선.addSection(역삼역_삼성역_구간);

        // when
        final Section 이호선_마지막_구간 = 이호선.getLastSection();

        // then
        assertThat(이호선_마지막_구간.getUpStation().getName()).isEqualTo("역삼역");
        assertThat(이호선_마지막_구간.getDownStation().getName()).isEqualTo("삼성역");
    }

    @DisplayName("구간 삭제")
    @Test
    void removeSection() {
        // Given
        final Section 강남역_역삼역_구간 = new Section(이호선, 강남역, 역삼역, 10);
        이호선.addSection(강남역_역삼역_구간);

        // When
        이호선.removeSectionWithValidateStation(역삼역);

        // Then
        final Sections 이호선_구간_리스트 = 이호선.getSections();
        assertThat(이호선_구간_리스트.getList()).hasSize(0);
    }
}
