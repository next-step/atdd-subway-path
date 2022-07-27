package nextstep.subway.domain;

import nextstep.subway.exception.NewlySectionUpStationAndDownStationNotExist;
import nextstep.subway.exception.SectionAllStationsAlreadyExistException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class SectionsTest {

    private Station 선릉역;

    private Station 삼성역;

    private Station 종합운동장역;

    private Line _2호선;

    @BeforeEach
    void setUp() {
        선릉역 = new Station("선릉역");
        삼성역 = new Station("삼성역");
        종합운동장역 = new Station("종합운동장역");
        _2호선 = new Line("2호선", "bg-green-600");
    }


    @DisplayName("구간 등록을 할 수 있다.")
    @Test
    void addSection() {
        //given

        final var firstSection = new Section(_2호선, 선릉역, 삼성역, 10);
        final var secondSection = new Section(_2호선, 삼성역, 종합운동장역, 3);

        //when
        final var sections = new Sections();
        sections.add(firstSection);
        sections.add(secondSection);

        //then
        assertThat(sections.getSections()).containsExactly(firstSection, secondSection);
    }

    @DisplayName("역 사이에 새로운 역을 등록할 수 있다.")
    @Test
    void addSectionBetweenSection() {
        //given

        final var firstSection = new Section(_2호선, 선릉역, 종합운동장역, 10);
        final var secondSection = new Section(_2호선, 선릉역, 삼성역, 3);

        //when
        final var sections = new Sections();
        sections.add(firstSection);
        sections.add(secondSection);

        //then
        assertAll(
                () -> assertThat(sections.getSections()).hasSize(2),
                () -> assertThat(sections.allStations()).containsExactly(선릉역, 삼성역, 종합운동장역)
        );
    }

    @DisplayName("구간의 모든 역을 찾을 수 있다.")
    @Test
    void allStations() {
        //given
        final var firstSection = new Section(_2호선, 선릉역, 삼성역, 10);
        final var secondSection = new Section(_2호선, 삼성역, 종합운동장역, 3);

        //when
        final var sections = new Sections();
        sections.add(firstSection);
        sections.add(secondSection);

        //then
        assertThat(sections.allStations()).containsExactly(선릉역, 삼성역, 종합운동장역);
    }

    @DisplayName("구간에 역의 id를 통해 존재여부를 파악할 수 있다.")
    @Test
    void hasStation() {
        //given
        final var firstSection = new Section(_2호선, 선릉역, 삼성역, 10);
        final var secondSection = new Section(_2호선, 삼성역, 종합운동장역, 3);

        //when
        final var sections = new Sections();
        sections.add(firstSection);
        sections.add(secondSection);

        //then
        assertAll(
                () -> assertThat(sections.hasStation(선릉역)).isTrue(),
                () -> assertThat(sections.hasStation(삼성역)).isTrue(),
                () -> assertThat(sections.hasStation(종합운동장역)).isTrue(),
                () -> assertThat(sections.hasStation(new Station("잠실역"))).isFalse()
        );
    }

    @DisplayName("하행역 삭제를 통해 구간을 삭제할 수 있다.")
    @Test
    void deleteSection() {
        //given
        final var firstSection = new Section(_2호선, 선릉역, 삼성역, 10);
        final var secondSection = new Section(_2호선, 삼성역, 종합운동장역, 3);

        final var sections = new Sections();
        sections.add(firstSection);
        sections.add(secondSection);

        //when
        sections.delete(종합운동장역);

        //then
        assertThat(sections.getSections()).hasSize(1);
    }

    @DisplayName("삭제 시 하행역이 아니면 삭제할 수 없다")
    @Test
    void cantDeleteNotDownStation() {
        //given
        final var firstSection = new Section(_2호선, 선릉역, 삼성역, 10);
        final var secondSection = new Section(_2호선, 삼성역, 종합운동장역, 3);

        final var sections = new Sections();
        sections.add(firstSection);
        sections.add(secondSection);

        //when, then
        assertThatThrownBy(() -> sections.delete(삼성역))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("하행역만 삭제할 수 있습니다.");
    }


    @DisplayName("삭제 시 상행역과 하행역만이 존재하면 삭제할 수 없다")
    @Test
    void cantDeleteOnlyOneStationInSections() {
        //given
        final var section = new Section(_2호선, 선릉역, 삼성역, 10);

        final var sections = new Sections();
        sections.add(section);

        //when, then
        assertThatThrownBy(() -> sections.delete(삼성역))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역만 존재하기 때문에 삭제할 수 없습니다.");
    }


    @DisplayName("이미 존재하는 구간을 추가 할 수 없다.")
    @Test
    void cantAddAlreadyExistSection() {
        //given
        final var section = new Section(_2호선, 선릉역, 삼성역, 10);

        final var sections = new Sections();
        sections.add(section);

        //when, then
        assertThatThrownBy(() -> sections.add(new Section(_2호선, 선릉역, 삼성역, 10)))
                .isInstanceOf(SectionAllStationsAlreadyExistException.class)
                .hasMessage("이미 구간 내 상행역, 하행역이 모두 존재하여 추가할 수 없습니다.");
    }


    @DisplayName("상행역, 하행역 어떤것도 존재하지 않을 때 구간 추가할 수 없다.")
    @Test
    void cantAddNoExistUpStationAndDownStation() {
        //given
        final var section = new Section(_2호선, 선릉역, 삼성역, 10);

        final var sections = new Sections();
        sections.add(section);


        //when, then
        final var 신설역1 = new Station("신설역1");
        final var 신설역2 = new Station("신설역2");
        assertThatThrownBy(() -> sections.add(new Section(_2호선, 신설역1, 신설역2, 5)))
                .isInstanceOf(NewlySectionUpStationAndDownStationNotExist.class)
                .hasMessage("추가하고자 하는 상행역, 하행역이 존재하지 않아 추가할 수 없습니다.");
    }
}