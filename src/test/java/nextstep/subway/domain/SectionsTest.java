package nextstep.subway.domain;

import nextstep.subway.domain.exception.NotExistSectionException;
import nextstep.subway.domain.exception.SectionDeleteException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionsTest {

    @Test
    @DisplayName("모든 구간의 지하철 역들을 반환한다.")
    void getStations() {
        //given
        Section section = new Section(StationTest.GANGNAM_STATION, StationTest.YEOKSAM_STATION, 10);
        Section section2 = new Section(StationTest.YEOKSAM_STATION, StationTest.SEOLLEUNG_STATION, 10);

        Sections sections = new Sections();
        sections.add(section);
        sections.add(section2);

        // then
        assertThat(sections.getStations()).containsOnly(new Station(1L, "강남역"),
                                                            new Station(2L, "역삼역"),
                                                            new Station(3L, "선릉역"));
    }

    @Test
    @DisplayName("구간을 삭제하면 하행종점역이 사라진다.")
    void delete_section() {
        // given
        Sections sections = new Sections();
        Section section = new Section(StationTest.GANGNAM_STATION, StationTest.YEOKSAM_STATION, 10);
        Section newSection = new Section(StationTest.YEOKSAM_STATION, StationTest.SEOLLEUNG_STATION, 5);

        sections.add(section);
        sections.add(newSection);

        // when
        sections.delete(StationTest.SEOLLEUNG_STATION);

        // then
        assertThat(sections.getStations()).doesNotContain(new Station(3L, "선릉역"));
    }

    @Test
    @DisplayName("삭제요청한 구간이 마지막 구간이 아니면 예외를 반환한다.")
    void invalid_delete_not_last_section() {
        // given
        Sections sections = new Sections();
        Section section = new Section(StationTest.GANGNAM_STATION, StationTest.YEOKSAM_STATION, 10);
        Section newSection = new Section(StationTest.YEOKSAM_STATION, StationTest.SEOLLEUNG_STATION, 5);

        sections.add(section);
        sections.add(newSection);

        // then
        assertThatThrownBy(() -> sections.delete(StationTest.YEOKSAM_STATION))
                .isInstanceOf(SectionDeleteException.class);
    }

    @Test
    @DisplayName("구간이 아무 것도 없을 때 삭제 시 예외를 반환한다.")
    void invalid_delete_not_found_section() {
        // given
        Sections sections = new Sections();

        // then
        assertThatThrownBy(() -> sections.delete(StationTest.YEOKSAM_STATION))
                .isInstanceOf(NotExistSectionException.class);
    }

}