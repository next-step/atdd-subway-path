package nextstep.subway.unit;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.SectionsIncludingStation;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static nextstep.subway.unit.LineFixtures.구간_생성;
import static nextstep.subway.unit.LineFixtures.역_생성;
import static org.assertj.core.api.Assertions.assertThat;

class SectionsIncludingStationTest {
    private Set<Section> sections = new HashSet<>();
    private Section 상행구간 = 구간_생성(1L, 1L, 2L, 100);
    private Section 하행구간 = 구간_생성(2L, 2L, 3L, 100);

    @BeforeEach
    void setUp() {
        // 1 - 2 - 3
        sections.add(상행구간);
        sections.add(하행구간);
    }

    @DisplayName("전체 구간들과 역 기준으로 상행, 하행 구간을 가진 구간 집합 객체가 생성된다.")
    @Test
    void getUpSectionAndgetDownSection() {
        // when
        SectionsIncludingStation sectionsIncludingStation = new SectionsIncludingStation(sections, 역_생성(2L));

        // then
        assertThat(sectionsIncludingStation.getUpSection().getId()).isEqualTo(1L);
        assertThat(sectionsIncludingStation.getDownSection().getId()).isEqualTo(2L);
    }

    @DisplayName("어떠한 역도 구간에 포함되어 있지 않으면 구간 집합 객체에는 어떠한 구간도 가지지 않는다.")
    @Test
    void hasNotAnySection() {
        // when
        SectionsIncludingStation sectionsIncludingStation = new SectionsIncludingStation(sections, 역_생성(3L));
        SectionsIncludingStation sectionsIncludingStation2 = new SectionsIncludingStation(sections, 역_생성(4L));

        // then
        assertThat(sectionsIncludingStation.hasNotAnySection()).isFalse();
        assertThat(sectionsIncludingStation2.hasNotAnySection()).isTrue();
    }


    @DisplayName("역이 한 구간에만 포함되어 있으면 구간 집합 객체에는 한 구간만 가진다")
    @Test
    void hasOnlyOneSection() {
        // when
        SectionsIncludingStation sectionsIncludingStation = new SectionsIncludingStation(sections, 역_생성(3L));

        // then
        assertThat(sectionsIncludingStation.hasOnlyOneSection()).isTrue();
    }

    @DisplayName("역이 포함된 어떠한 한 구간만 가져온다. (만약 두 구간을 가지면 상행 구간을 우선으로 가져온다)")
    @Test
    void getAnyOneSectionWhenHasTwoSection() {
        // given
        Station 구간의_중간역 = 역_생성(2L);

        // when
        SectionsIncludingStation sectionsIncludingStation = new SectionsIncludingStation(sections, 구간의_중간역);

        // then
        assertThat(sectionsIncludingStation.getAnyOneSection()).isEqualTo(상행구간);
    }

    @DisplayName("역이 포함된 어떠한 한 구간만 가져온다.")
    @Test
    void getAnyOneSection() {
        // given
        Station 구간의_상행역 = 역_생성(1L);
        Station 구간의_하행역 = 역_생성(3L);

        // when
        SectionsIncludingStation sectionsIncludingStation = new SectionsIncludingStation(sections, 구간의_상행역);
        SectionsIncludingStation sectionsIncludingStation2 = new SectionsIncludingStation(sections, 구간의_하행역);

        // then
        assertThat(sectionsIncludingStation.getAnyOneSection()).isEqualTo(상행구간);
        assertThat(sectionsIncludingStation2.getAnyOneSection()).isEqualTo(하행구간);
    }
}
