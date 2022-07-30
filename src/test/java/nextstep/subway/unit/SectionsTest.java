package nextstep.subway.unit;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.SectionRegistrationException;
import nextstep.subway.exception.SectionRemovalException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static nextstep.subway.utils.GivenUtils.FIVE;
import static nextstep.subway.utils.GivenUtils.강남_역삼_구간;
import static nextstep.subway.utils.GivenUtils.강남역;
import static nextstep.subway.utils.GivenUtils.강남역_이름;
import static nextstep.subway.utils.GivenUtils.선릉역;
import static nextstep.subway.utils.GivenUtils.양재역;
import static nextstep.subway.utils.GivenUtils.역삼_선릉_구간;
import static nextstep.subway.utils.GivenUtils.역삼역;
import static nextstep.subway.utils.GivenUtils.역삼역_이름;
import static nextstep.subway.utils.GivenUtils.이호선;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SectionsTest {

    @Test
    @DisplayName("section 추가 - 성공적인 추가")
    void addSection() {
        // given
        int expectedSize = 2;
        Sections sections = new Sections();
        Section section = 강남_역삼_구간();

        // when
        sections.add(section);

        // then
        List<String> stationNames = getStationNames(sections);
        assertThat(stationNames).hasSize(expectedSize)
                .containsExactly(강남역_이름, 역삼역_이름);
    }

    @Test
    @DisplayName("section 추가 - 노선의 상행,하행 역과 무관한 역 추가")
    void addSectionWithInvalidUpStationId() {
        // given
        Sections sections = new Sections();
        sections.add(강남_역삼_구간());
        Section invalidSection = new Section(이호선(), 선릉역(), 양재역(), FIVE);

        // when
        Executable executable = () -> sections.add(invalidSection);

        // then
        assertThrows(SectionRegistrationException.class, executable);
    }

    @Test
    @DisplayName("section 추가 - sections 에 이미 존재하는 downStationId")
    void addSectionWithInvalidDownStationId() {
        // given
        Sections sections = new Sections();
        sections.add(강남_역삼_구간());
        Section invalidSection = new Section(이호선(), 역삼역(), 강남역(), FIVE);

        // when
        Executable executable = () -> sections.add(invalidSection);

        // then
        assertThrows(SectionRegistrationException.class, executable);
    }

    @Test
    @DisplayName("section 제거 - 마지막 역 제거")
    void removeSection() {
        // given
        int expectedSize = 2;
        Sections sections = new Sections();
        sections.add(강남_역삼_구간());
        sections.add(역삼_선릉_구간());

        // when
        sections.removeSection(선릉역());

        // then
        assertThat(getStationNames(sections)).hasSize(expectedSize)
                .containsExactly(강남역_이름, 역삼역_이름);
    }

    @Test
    @DisplayName("section 제거 - 존재하지 않는 역 제거")
    void removeSectionWithInvalidLastStation() {
        // given
        Sections sections = new Sections();
        sections.add(강남_역삼_구간());
        sections.add(역삼_선릉_구간());

        // when
        Executable executable = () -> sections.removeSection(양재역());

        // then
        assertThrows(NoSuchElementException.class, executable);
    }

    @Test
    @DisplayName("section 제거 - 구간이 1개인 경우")
    void removeSingleSection() {
        // given
        Sections sections = new Sections();
        sections.add(강남_역삼_구간());

        // when
        Executable executable = () -> sections.removeSection(역삼역());

        // then
        assertThrows(SectionRemovalException.class, executable);
    }

    private List<String> getStationNames(Sections sections) {
        return sections.getStations()
                .stream()
                .map(Station::getName)
                .collect(Collectors.toList());
    }

}