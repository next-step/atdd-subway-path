package nextstep.subway.unit.domain;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class SectionsTest {
    private Line 이호선;
    private Station 강남역;
    private Station 삼성역;
    private Station 잠실역;
    private Section section;

    private Sections sections;
    @BeforeEach
    void setUp() {
        sections = new Sections();

        이호선 = new Line("이호선", "green");

        강남역 = new Station("강남역");
        삼성역 = new Station("삼성역");
        section = new Section(이호선, 강남역, 삼성역, 10);

        잠실역 = new Station("잠실역");
    }

    @Test
    void add_to_구간이_빈_노선() {
        // When
        sections.add(section);

        // Then
        assertThat(sections.size()).isEqualTo(1);
    }

    @Test
    void add_하행종착역_뒤() {
        // Given
        sections.add(section);

        // When
        Section 하행종착역_뒤_section = new Section(이호선, 삼성역, 잠실역, 10);
        sections.add(하행종착역_뒤_section);

        // Then
        assertThat(sections.size()).isEqualTo(2);
    }

    @Test
    void add_상행종착역_앞() {
        // Given
        sections.add(section);

        // When
        Section 하행종착역_뒤_section = new Section(이호선, 잠실역, 강남역, 10);
        sections.add(하행종착역_뒤_section);

        // Then
        assertThat(sections.size()).isEqualTo(2);
    }

    @Test
    void add_하행종착역_앞() {
        // Given
        sections.add(section);

        // When
        Section 하행종착역_뒤_section = new Section(이호선, 잠실역, 삼성역, 6);
        sections.add(하행종착역_뒤_section);

        // Then
        assertThat(sections.size()).isEqualTo(2);
    }

    @Test
    void add_상행종착역_뒤() {
        // Given
        sections.add(section);

        // When
        Section 하행종착역_뒤_section = new Section(이호선, 강남역, 잠실역, 6);
        sections.add(하행종착역_뒤_section);

        // Then
        assertThat(sections.size()).isEqualTo(2);
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 12})
    void add_exception_기준역의_구간보다_크거나_같을때(int distance) {
        // Given
        sections.add(section);
        Section 상행종착역_뒤_section = new Section(이호선, 강남역, 잠실역, distance);

        // When & Then
        assertThatThrownBy(() -> sections.add(상행종착역_뒤_section))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void add_exception_기존_구간들에_이미_존재하는_역일때() {
        // Given
        sections.add(section);
        Section newSection = new Section(이호선, 강남역, 삼성역, 10);

        // When & Then
        assertThatThrownBy(() -> sections.add(newSection))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void add_exception_기준할_역이_없을때() {
        // Given
        sections.add(section);

        Station 부산역 = new Station("부산역");
        Station 강원역 = new Station("강원역");
        Section newSection = new Section(이호선, 부산역, 강원역, 6);

        // When & Then
        assertThatThrownBy(() -> sections.add(newSection))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("모든 역을 조회한다. 상행종착역부터 하행종착역까지의 순서를 보장한다")
    @Test
    void getStations() {
        // Given
        Section 하행종착역_뒤_section = new Section(이호선, 잠실역, 삼성역, 6);

        sections.add(section);
        sections.add(하행종착역_뒤_section);

        // When
        List<Station> stations = sections.getStations();

        // Then
        assertAll(
                () -> assertThat(stations).hasSize(3),
                () -> assertThat(stations).containsExactly(강남역, 잠실역, 삼성역)
        );
    }

    @Test
    void remove() {
    }


}
