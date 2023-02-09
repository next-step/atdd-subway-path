package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.exception.SubwayException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;


@DisplayName("구간 단위 테스트")
class LineTest {
    private static final int DEFAULT_DISTANCE = 10;

    private Line line;
    private Station 강남역;
    private Station 역삼역;
    private Station 교대역;
    private Station 양재역;

    @BeforeEach
    void setUp() {
        line = new Line("2호선", "green");
        강남역 = new Station(1L, "강남역");
        역삼역 = new Station(2L, "역삼역");
        교대역 = new Station(3L, "교대역");
        양재역 = new Station(4L, "양재역");
    }

    @DisplayName("노선에 구간을 추가한다.")
    @Test
    void addSection() {
        Section 강남역_역삼역_구간 = new Section(강남역, 역삼역, DEFAULT_DISTANCE);

        line.addSection(강남역_역삼역_구간);

        assertAll(() -> {
            assertThat(line.sections()).hasSize(1);
            assertThat(강남역_역삼역_구간.getLine()).isEqualTo(line);
        });
    }

    @DisplayName("기존 구간의 역을 기준으로 새로운 구간을 추가한다.")
    @Test
    void addBetweenSection() {
        Section 강남역_교대역_구간 = new Section(강남역, 교대역, DEFAULT_DISTANCE);
        Section 강남역_역삼역_구간 = new Section(강남역, 역삼역, 5);
        line.addSection(강남역_교대역_구간);

        line.addSection(강남역_역삼역_구간);

        assertThat(line.getStations()).containsExactly(강남역, 역삼역, 교대역);
    }

    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    @Test
    void addUpEndSection() {
        Section 강남역_교대역_구간 = new Section(강남역, 교대역, DEFAULT_DISTANCE);
        Section 역삼역_강남역_구간 = new Section(역삼역, 강남역, DEFAULT_DISTANCE);
        line.addSection(강남역_교대역_구간);

        line.addSection(역삼역_강남역_구간);

        assertThat(line.getStations()).containsExactly(역삼역, 강남역, 교대역);
    }

    @DisplayName("새로운 역을 하행 종점으로 등록한다.")
    @Test
    void addDownEndSection() {
        Section 강남역_교대역_구간 = new Section(강남역, 교대역, DEFAULT_DISTANCE);
        Section 교대역_역삼역_구간 = new Section(교대역, 역삼역, DEFAULT_DISTANCE);
        line.addSection(강남역_교대역_구간);

        line.addSection(교대역_역삼역_구간);

        assertThat(line.getStations()).containsExactly(강남역, 교대역, 역삼역);
    }

    @DisplayName("구간을 추가할 때 기존 역 사이 길이보다 크거나 같으면 예외가 발생한다.")
    @ValueSource(ints = {DEFAULT_DISTANCE, 15})
    @ParameterizedTest(name = "구간 길이 {0} 입력")
    void addSectionException1(int distance) {
        Section 강남역_역삼역_구간 = new Section(강남역, 역삼역, DEFAULT_DISTANCE);
        Section 교대역_역삼역_구간 = new Section(교대역, 역삼역, distance);
        line.addSection(강남역_역삼역_구간);

        assertThatThrownBy(() -> line.addSection(교대역_역삼역_구간))
                .isInstanceOf(SubwayException.class);
    }

    @DisplayName("구간을 추가할 때 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 예외가 발생한다.")
    @Test
    void addSectionException2() {
        Section 강남역_역삼역_구간 = new Section(강남역, 역삼역, DEFAULT_DISTANCE);
        Section 교대역_양재역_구간 = new Section(교대역, 양재역, DEFAULT_DISTANCE);
        line.addSection(강남역_역삼역_구간);

        assertThatThrownBy(() -> line.addSection(교대역_양재역_구간))
                .isInstanceOf(SubwayException.class);
    }

    @DisplayName("구간을 추가할 때 상행역과 하행역이 이미 노선에 모두 등록되어 있으면 예외가 발생한다.")
    @Test
    void addSectionException3() {
        Section 강남역_역삼역_구간 = new Section(강남역, 역삼역, DEFAULT_DISTANCE);
        line.addSection(강남역_역삼역_구간);

        assertThatThrownBy(() -> line.addSection(강남역_역삼역_구간))
                .isInstanceOf(SubwayException.class);
    }

    @DisplayName("노선에 포함된 역을 찾을 수 있다.")
    @Test
    void getStations() {
        Section 강남역_역삼역_구간 = new Section(강남역, 역삼역, DEFAULT_DISTANCE);
        Section 역삼역_교대역_구간 = new Section(역삼역, 교대역, DEFAULT_DISTANCE);

        line.addSection(강남역_역삼역_구간);
        line.addSection(역삼역_교대역_구간);

        assertThat(line.getStations())
                .containsExactly(강남역, 역삼역, 교대역);
    }

    @DisplayName("노선에 있는 구간을 제거할 수 있다.")
    @Test
    void removeSection() {
        Section 강남역_역삼역_구간 = new Section(강남역, 역삼역, DEFAULT_DISTANCE);
        Section 역삼역_교대역_구간 = new Section(역삼역, 교대역, DEFAULT_DISTANCE);
        line.addSection(강남역_역삼역_구간);
        line.addSection(역삼역_교대역_구간);

        line.removeSection(교대역.getId());

        assertThat(line.sections()).containsExactly(강남역_역삼역_구간);
    }
}
