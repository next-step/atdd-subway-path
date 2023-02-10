package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.domain.Line.EXCEPTION_MESSAGE_CAN_REMOVE_TAIL_STATION;
import static nextstep.subway.domain.Line.EXCEPTION_MESSAGE_MINIMUM_ONE_SECTION_REQUIRED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 구간 단위 테스트")
class LineTest {

    private Line 이호선;
    private List<Section> sections;
    private Station 강남역;
    private Station 삼성역;
    private Station 잠실역;

    @BeforeEach
    void setUp() {
        // Given
        강남역 = new Station("강남역");
        삼성역 = new Station("삼성역");
        잠실역 = new Station("잠실역");

        이호선 = new Line("2호선", "green");
        이호선.addSection(new Section(이호선, 강남역, 삼성역, 10));

        sections = 이호선.getSections();
    }

    @DisplayName("지하철노선의 구간을 추가한다.")
    @Test
    void addSection() {
        // Given
        int beforeSize = sections.size();

        // When
        이호선.addSection(new Section(이호선, 삼성역, 잠실역, 7));

        // Then
        sections = 이호선.getSections();
        assertThat(sections).hasSize(beforeSize + 1);
    }

    @DisplayName("지하철노선의 역을 조회한다.")
    @Test
    void getStations() {
        // When
        List<Station> stations = 이호선.getStations();

        // Then
        assertAll(
                () -> assertThat(stations).hasSize(2),
                () -> assertThat(stations).containsExactly(강남역, 삼성역)
        );
    }

    @DisplayName("지하철노선의 구간을 제거한다.")
    @Test
    void removeSection() {
        // Given
        이호선.addSection(new Section(이호선, 삼성역, 잠실역, 7));
        int beforeSize = sections.size();

        // When
        이호선.removeSection(잠실역);

        // Then
        sections = 이호선.getSections();
        assertThat(sections).hasSize(beforeSize - 1);
    }

    @DisplayName("지하철노선의 하행종점역만 제거할 수 있다.")
    @Test
    void removeSectionException1() {
        // Given
        이호선.addSection(new Section(이호선, 삼성역, 잠실역, 7));

        // When & Then
        assertThatThrownBy(() -> 이호선.removeSection(삼성역))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(EXCEPTION_MESSAGE_CAN_REMOVE_TAIL_STATION);
    }

    @DisplayName("지하철노선의 구간이 1개만 존재한다면, 구간을 제거할 수 없다.")
    @Test
    void removeSectionException2() {
        // When & Then
        assertThatThrownBy(() -> 이호선.removeSection(삼성역))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(EXCEPTION_MESSAGE_MINIMUM_ONE_SECTION_REQUIRED);
    }
}
