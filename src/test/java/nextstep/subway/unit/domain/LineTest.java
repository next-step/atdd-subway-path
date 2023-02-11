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

import static nextstep.subway.domain.Sections.EXCEPTION_MESSAGE_CAN_REMOVE_TAIL_STATION;
import static nextstep.subway.domain.Sections.EXCEPTION_MESSAGE_MINIMUM_ONE_SECTION_REQUIRED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 구간 단위 테스트")
class LineTest {

    private Line 이호선;
    private Sections sections;
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
        assertThat(이호선.getSections().size()).isEqualTo(beforeSize + 1);
    }

    /**
     * Given 기존 구간보다 크거나 같은 길이의 구간을
     * When 지하철 노선에 새로운 구간 추가시
     * Then 예외가 발생한다
     *
     * Case 1
     * 강남역 <--------10-------> 잠실역
     *                           ↓
     *                           X
     * 강남역 <--------10-------> 삼성역
     *
     * Case 2
     * 강남역 <------------12-----------> 잠실역
     *                                    ↓
     *                                    X
     * 강남역 <--------10-------> 삼성역
     */
    @DisplayName("기존 구간보다 크거나 같으면 구간 추가시 예외발생")
    @ParameterizedTest
    @ValueSource(ints = {10, 12})
    void addSectionEx1(int distance) {
        // Given
        Section section = new Section(이호선, 강남역, 잠실역, distance);

        // When & Then
        assertThatThrownBy(() -> 이호선.addSection(section))
                .isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Given 노선에 이미 등록되어있는 역의 구간을
     * When 지하철 노선에 새로운 구간 추가시
     * Then 예외가 발생한다
     *
     * 강남역 <--------10-------> 삼성역
     *   ↓                        ↓
     *   X                        X
     * 강남역 <--------10-------> 삼성역
     */
    @DisplayName("기존 구간와 같은 구간 추가시 예외발생")
    @Test
    void addSectionEx2() {
        // Given
        Section section = new Section(이호선, 강남역, 삼성역, 10);

        // When & Then
        assertThatThrownBy(() -> 이호선.addSection(section))
                .isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Given 노선에 존재하지 않는 역들로 구성된 구간을
     * When 지하철 노선에 새로운 구간 추가시
     * Then 예외가 발생한다
     *
     *       부산역 <---6---> 강원역
     *         ↓              ↓
     *         X              X
     * 강남역 <--------10-------> 삼성역
     */
    @DisplayName("노선에서 찾을 수 없는 역들의 구간 추가시 예외발생")
    @Test
    void addSectionEx3() {
        // Given
        Station 부산역 = new Station("부산역");
        Station 강원역 = new Station("강원역");
        Section section = new Section(이호선, 부산역, 강원역, 6);

        // When & Then
        assertThatThrownBy(() -> 이호선.addSection(section))
                .isInstanceOf(IllegalArgumentException.class);
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

    /**
     * As-is
     * 강남역 <----6----> 잠실역
     *                    ↓
     * 강남역 <---------10---------> 삼성역
     *
     * To-be
     * 강남역 <----6----> 잠실역 <-4-> 삼성역
     *
     * 노선 조회 시, 강남역, 잠실역, 삼성역 순서 보장해야한다
     */
    @DisplayName("지하철노선의 역을 조회시, 상행 종착역부터 하행 종착역까지 순서이다.")
    @Test
    void getStations2() {
        // Given
        Section section = new Section(이호선, 강남역, 잠실역, 6);
        sections.add(section);

        // When
        List<Station> stations = 이호선.getStations();

        // Then
        assertAll(
                () -> assertThat(stations).hasSize(3),
                () -> assertThat(stations).contains(강남역, 잠실역, 삼성역)
        );
    }

    @DisplayName("지하철노선의 구간을 제거한다.")
    @Test
    void removeSection() {
        // Given
        이호선.addSection(new Section(이호선, 삼성역, 잠실역, 7));
        int beforeSize = 이호선.getSections().size();

        // When
        이호선.removeSection(잠실역);

        // Then
        assertThat(이호선.getSections().size()).isEqualTo(beforeSize - 1);
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
