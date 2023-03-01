package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class LineTest {
    private Station 강남역;
    private Station 판교역;
    private Line 신분당선;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        판교역 = new Station("판교역");
        신분당선 = new Line("신분당선", "red");
        신분당선.addSection(강남역, 판교역, 10);
    }

    @DisplayName("지하철 노선에 구간을 추가한다.")
    @Test
    void addSection() {
        // then
        assertThat(신분당선.getSections()).containsExactly(new Section(신분당선, 강남역, 판교역, 10));
    }

    @DisplayName("지하철 노선에 음수, 혹은 0인 거리의 구간은 추가할 수 없다.")
    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void addSectionExceptionWhenNegativeDistance(int distance) {
        // when & then
        Station 정자역 = new Station("정자역");
        assertThatThrownBy(() -> 신분당선.addSection(판교역, 정자역, distance)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("지하철 노선의 역 사이에 새로운 역을 등록한다.")
    @Test
    void addLineSectionWithinSection() {
        // given
        Station 양재역 = new Station("양재역");
        Station 청계산입구역 = new Station("청계산입구역");

        // when
        신분당선.addSection(강남역, 양재역, 4);
        신분당선.addSection(청계산입구역, 판교역, 3);

        // then
        assertThat(신분당선.getSections()).containsExactlyInAnyOrder(
                new Section(신분당선, 강남역, 양재역, 4),
                new Section(신분당선, 양재역, 청계산입구역, 3),
                new Section(신분당선, 청계산입구역, 판교역, 3)
        );
    }

    @DisplayName("지하철 노선의 새로운 상행 종점역 구간을 등록한다.")
    @Test
    void addLineSectionBeforeFirstStation() {
        // given
        Station 신논현역 = new Station("신논현역");

        // when
        신분당선.addSection(신논현역, 강남역, 2);

        // then
        assertThat(신분당선.getSections()).containsExactlyInAnyOrder(
                new Section(신분당선, 강남역, 판교역, 10),
                new Section(신분당선, 신논현역, 강남역, 2)
        );
    }

    @DisplayName("역 사이에 새로운 역 등록 시, 기존 역 사이 길이 보다 크거나 같은 경우 구간을 등록할 수 없다.")
    @ParameterizedTest
    @ValueSource(ints = {10, 12})
    void addLineSectionExceptionDistanceMoreThanExistSection(int distance) {
        // given
        Station 정자역 = new Station("정자역");

        // when & then
        assertThatThrownBy(() -> 신분당선.addSection(강남역, 정자역, distance))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("등록할 구간의 역이 모두 노선에 등록된 경우, 구간을 등록할 수 없다.")
    @Test
    void addLineSectionExceptionAllStationInLine() {
        // when & then
        assertThatThrownBy(() -> 신분당선.addSection(강남역, 판교역, 10))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("등록할 구간의 역이 노선에 하나도 포함되지 않았다면, 구간을 등록할 수 없다.")
    @Test
    void addLineSectionExceptionNotContainsAllStation() {
        // given
        Station 신사역 = new Station("신사역");
        Station 신논현역 = new Station("신논현역");

        // when & then
        assertThatThrownBy(() -> 신분당선.addSection(신사역, 신논현역, 3))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("지하철 노선의 새로운 하행 종점역 구간을 등록한다.")
    @Test
    void addLineSectionAfterFinalStation() {
        // given
        Station 정자역 = new Station("정자역");

        // when
        신분당선.addSection(판교역, 정자역, 2);

        // then
        assertThat(신분당선.getSections()).containsExactlyInAnyOrder(
                new Section(신분당선, 강남역, 판교역, 10),
                new Section(신분당선, 판교역, 정자역, 2)
        );
    }

    @DisplayName("지하철 노선에 등록된 역들을 조회한다.")
    @Test
    void getStations() {
        // when & then
        assertThat(신분당선.getStations()).containsExactly(강남역, 판교역);
    }

    @DisplayName("지하철 노선에 등록된 순서와 상관없이 상행 종점역 부터 하행 종점역까지의 정렬된 역을 조회한다.")
    @Test
    void getStationsInOrder() {
        // given
        Station 신사역 = new Station("신사역");
        Station 정자역 = new Station("정자역");
        신분당선.addSection(판교역, 정자역, 3);
        신분당선.addSection(신사역, 강남역, 3);

        // when & then
        assertThat(신분당선.getStations()).containsExactly(신사역, 강남역, 판교역, 정자역);
    }

    @DisplayName("지하철 노선에 등록된 역들 중, 중간 역을 제거한다.")
    @Test
    void removeLineSectionWithinStation() {
        // given
        Station 정자역 = new Station("정자역");
        신분당선.addSection(판교역, 정자역, 3);

        // when
        신분당선.removeSection(판교역);

        // then
        assertThat(신분당선.getSections()).containsExactlyInAnyOrder(
                new Section(신분당선, 강남역, 정자역, 13)
        );
    }

    @DisplayName("지하철 노선에 등록된 상행, 하행 종점역을 제거한다.")
    @Test
    void removeLineFirstAndFinalSection() {
        // given
        Station 신사역 = new Station("신사역");
        Station 정자역 = new Station("정자역");
        신분당선.addSection(신사역, 강남역, 5);
        신분당선.addSection(판교역, 정자역, 3);

        // when
        신분당선.removeSection(신사역);
        신분당선.removeSection(정자역);

        // then
        assertThat(신분당선.getSections()).containsExactlyInAnyOrder(
                new Section(신분당선, 강남역, 판교역, 10)
        );
    }

    @DisplayName("구간 제거 시, 노선에 등록된 구간이 하나라면 구간을 제거할 수 없다.")
    @Test
    void removeLineSectionExceptionOnlyOneSection() {
        // when & then
        assertAll(
                () -> assertThatThrownBy(() -> 신분당선.removeSection(강남역)).isInstanceOf(IllegalStateException.class),
                () -> assertThatThrownBy(() -> 신분당선.removeSection(판교역)).isInstanceOf(IllegalStateException.class)
        );
    }

    @DisplayName("구간 제거 시, 노선에 등록된 역이 없다면 구간을 제거할 수 없다.")
    @Test
    void removeLineSectionExceptionStationNotInLine() {
        // given
        Station 정자역 = new Station("정자역");
        Station 신사역 = new Station("신사역");
        신분당선.addSection(판교역, 정자역, 3);

        // when & then
        assertThatThrownBy(() -> 신분당선.removeSection(신사역)).isInstanceOf(IllegalArgumentException.class);
    }
}
