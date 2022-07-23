package nextstep.subway.domain;

import nextstep.subway.domain.exception.NotExistSectionException;
import nextstep.subway.domain.exception.SectionDeleteException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.domain.factory.SectionFactory.createSection;
import static nextstep.subway.domain.fixture.StationFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class SectionsTest {

    @Test
    @DisplayName("모든 구간의 지하철 역을 연결된 순서에 맞게 반환한다.")
    void getStations() {
        // given
        Sections sections = new Sections();
        Section section = createSection(GANGNAM_STATION, YEOKSAM_STATION, 10);
        sections.add(section);

        Section betweenSection = createSection(GANGNAM_STATION, SAMSUNG_STATION, 3);
        sections.add(betweenSection);

        // then
        assertThat(sections.getStations()).containsExactly(new Station(1L, "강남역"),
                new Station(4L, "삼성역"),
                new Station(2L, "역삼역"));
    }

    @Test
    @DisplayName("구간이 추가되면 새로운 구간의 상행역과 하행역을 조회할 수 있다.")
    void new_section() {
        // given
        Sections sections = new Sections();

        // when
        Section section = createSection(GANGNAM_STATION, YEOKSAM_STATION, 10);
        sections.add(section);

        // then
        assertThat(sections.getStations()).containsOnly(new Station(1L, "강남역"),
                new Station(2L, "역삼역"));
    }

    @Test
    @DisplayName("하나 이상의 구간을 가진 경우 신규 구간의 상행역과 하행역 둘중 하나만 포함해야 한다.")
    void invalid_add_section_match_only_one_station() {
        // given
        Sections sections = new Sections();
        Section section = createSection(GANGNAM_STATION, YEOKSAM_STATION, 10);
        sections.add(section);

        // when
        Section notContainStationSection = createSection(SEOLLEUNG_STATION, SAMSUNG_STATION, 5);
        Section containBothStationSection = createSection(GANGNAM_STATION, YEOKSAM_STATION, 5);

        // then
        assertAll(() -> {
            assertThatThrownBy(() -> sections.add(notContainStationSection)).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> sections.add(containBothStationSection)).isInstanceOf(IllegalArgumentException.class);
        });
    }

    @Test
    @DisplayName("구간 사이에 등록 시 신규 구간의 길이가 구간 사이의 길이보다 작아야한다.")
    void invalid_add_section_wrong_distance() {
        // given
        Sections sections = new Sections();
        Section section = createSection(GANGNAM_STATION, YEOKSAM_STATION, 10);
        sections.add(section);

        // when
        Section betweenSection = createSection(GANGNAM_STATION, SAMSUNG_STATION, 15);

        // then
        assertThatThrownBy(() -> sections.add(betweenSection))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("구간 사이에 등록 하면 신규 구간의 지하철 역이 추가된다.")
    void add_section_between_section() {
        // given
        Sections sections = new Sections();
        Section section = createSection(GANGNAM_STATION, YEOKSAM_STATION, 10);
        sections.add(section);

        // when
        Section betweenSection = createSection(GANGNAM_STATION, SAMSUNG_STATION, 3);
        sections.add(betweenSection);

        // then
        assertThat(sections.getStations()).containsOnly(GANGNAM_STATION, YEOKSAM_STATION, SAMSUNG_STATION);
    }

    @Test
    @DisplayName("상행 좀점역에 등록 하면 신규 구간의 지하철 역이 추가되고 상행종점역이 바뀐다.")
    void add_section_up_section() {
        // given
        Sections sections = new Sections();
        Section section = createSection(GANGNAM_STATION, YEOKSAM_STATION, 10);
        sections.add(section);

        // when
        Section betweenSection = createSection(SAMSUNG_STATION, GANGNAM_STATION, 3);
        sections.add(betweenSection);

        // then
        assertThat(sections.getStations()).containsExactly(SAMSUNG_STATION, GANGNAM_STATION, YEOKSAM_STATION);
    }

    @Test
    @DisplayName("햐행 좀점역에 등록 하면 신규 구간의 지하철 역이 추가되고 하행종점역이 바뀐다.")
    void add_section_down_section() {
        // given
        Sections sections = new Sections();
        Section section = createSection(GANGNAM_STATION, YEOKSAM_STATION, 10);
        sections.add(section);

        // when
        Section betweenSection = createSection(YEOKSAM_STATION, SAMSUNG_STATION, 13);
        sections.add(betweenSection);

        // then
        assertThat(sections.getStations()).containsExactly(GANGNAM_STATION, YEOKSAM_STATION, SAMSUNG_STATION);
    }

    @Test
    @DisplayName("구간을 삭제하면 하행종점역이 사라진다.")
    void delete_section() {
        // given
        Sections sections = new Sections();
        Section section = createSection(GANGNAM_STATION, YEOKSAM_STATION, 10);
        Section newSection = createSection(YEOKSAM_STATION, SEOLLEUNG_STATION, 5);

        sections.add(section);
        sections.add(newSection);

        // when
        sections.delete(SEOLLEUNG_STATION);

        // then
        assertThat(sections.getStations()).doesNotContain(new Station(3L, "선릉역"));
    }

    @Test
    @DisplayName("삭제요청한 구간이 마지막 구간이 아니면 예외를 반환한다.")
    void invalid_delete_not_last_section() {
        // given
        Sections sections = new Sections();
        Section section = createSection(GANGNAM_STATION, YEOKSAM_STATION, 10);
        Section newSection = createSection(YEOKSAM_STATION, SEOLLEUNG_STATION, 5);

        sections.add(section);
        sections.add(newSection);

        // then
        assertThatThrownBy(() -> sections.delete(YEOKSAM_STATION))
                .isInstanceOf(SectionDeleteException.class);
    }

    @Test
    @DisplayName("구간이 아무 것도 없을 때 삭제 시 예외를 반환한다.")
    void invalid_delete_not_found_section() {
        // given
        Sections sections = new Sections();

        // then
        assertThatThrownBy(() -> sections.delete(YEOKSAM_STATION))
                .isInstanceOf(NotExistSectionException.class);
    }

}