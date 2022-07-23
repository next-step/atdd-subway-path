package nextstep.subway.domain;

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
}