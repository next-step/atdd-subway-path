package nextstep.subway.domain;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.domain.factory.EntityFactory.createSection;
import static nextstep.subway.domain.factory.EntityFactory.createStation;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("노선의 구간을 갖는 집합에 대한 단위테스트")
class SectionsTest {
    private Station 강남역;
    private Station 선릉역;
    private Station 역삼역;
    private Line 이호선;
    private Sections sections;

    @BeforeEach
    void init() {
        강남역 = createStation(1L, "강남역");
        선릉역 = createStation(2L, "선릉역");
        역삼역 = createStation(3L, "역삼역");
        이호선 = Line.of("2호선", "green", 강남역, 선릉역, 10);

        sections = 이호선.getSections();
    }

    @Test
    @DisplayName("구간을 추가한다.")
    void addSection() {
        // given
        Section 추가할구간 = createSection(이호선, 선릉역, 역삼역, 7);

        // when
        sections.add(추가할구간);

        // then
        assertThat(sections.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("구간을 삭제한다.")
    void removeSection() {
        // given
        Section 삭제할구간 = createSection(이호선, 선릉역, 역삼역, 7);
        sections.add(삭제할구간);

        // when
        sections.remove(삭제할구간);

        // then
        assertThat(sections.size()).isEqualTo(1);
        assertThat(sections.getStations(강남역)).containsExactly(Arrays.array(강남역, 선릉역));
    }

    @Test
    @DisplayName("노선의 모든 구간의 지하철역을 순서대로 반환한다.")
    void getStations() {
        // given
        Section 추가할구간 = createSection(이호선, 선릉역, 역삼역, 7);
        sections.add(추가할구간);

        // when
        List<Station> stations = sections.getStations(강남역);

        // then
        assertThat(stations).containsExactly(Arrays.array(강남역, 선릉역, 역삼역));
    }

    @Test
    @DisplayName("노선에 역이 존재하는지 확인한다.")
    void hasStation() {
        // given
        Section 추가할구간 = createSection(이호선, 선릉역, 역삼역, 7);
        sections.add(추가할구간);

        // when/then
        assertThat(sections.hasStation(역삼역)).isTrue();
    }

    @Test
    @DisplayName("주어진 역을 하행으로 갖는 구간을 반환한다.")
    void findSectionByDownStation() {
        // given
        Section 추가할구간 = createSection(이호선, 선릉역, 역삼역, 7);
        sections.add(추가할구간);

        // when
        Section section = sections.findSectionByDownStation(역삼역);

        // then
        assertThat(section.getUpStation()).isEqualTo(선릉역);
        assertThat(section.getDownStation()).isEqualTo(역삼역);
    }
}