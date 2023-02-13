package nextstep.subway.unit.domain.sections;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class SectionsTest {
    private Line 이호선;
    private Station 강남역;
    private Station 삼성역;
    private Station 잠실역;

    private Sections sections;
    @BeforeEach
    void setUp() {
        이호선 = new Line("이호선", "green");

        강남역 = new Station("강남역");
        삼성역 = new Station("삼성역");

        sections = new Sections();
        sections.add(new Section(이호선, 강남역, 삼성역, 10));

        잠실역 = new Station("잠실역");
    }

    @DisplayName("모든 역을 조회한다. 상행종착역부터 하행종착역까지의 순서를 보장한다")
    @Test
    void getStations() {
        // Given
        Section 하행종착역_뒤_section = new Section(이호선, 잠실역, 삼성역, 6);

        sections.add(하행종착역_뒤_section);

        // When
        List<Station> stations = sections.getStations();

        // Then
        assertAll(
                () -> assertThat(stations).hasSize(3),
                () -> assertThat(stations).containsExactly(강남역, 잠실역, 삼성역)
        );
    }
}
