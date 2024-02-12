package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static nextstep.subway.fixture.LineFixture.BUNDANG_LINE;
import static nextstep.subway.fixture.LineFixture.SHINBUNDANG_LINE;
import static nextstep.subway.fixture.StationFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SectionTest {

    private static Section 강남역_선릉역_구간;
    private static Station 강남역;
    private static Station 선릉역;
    private static Station 양재역;
    private static Line 신분당선;
    private static Line 분당선;

    @BeforeAll
    static void setUp() {
        강남역 = GANGNAM_STATION.toStation(1L);
        선릉역 = SEOLLEUNG_STATION.toStation(2L);
        양재역 = YANGJAE_STATION.toStation(3L);
        신분당선 = SHINBUNDANG_LINE.toLine(1L);
        분당선 = BUNDANG_LINE.toLine(2L);
        강남역_선릉역_구간 = new Section(
                신분당선,
                강남역,
                선릉역,
                10L
        );
    }

    /**
     * 지하철역: 강남역, 선릉역, 양재역
     * 신분당선: 강남 - 선릉 (10)
     * total distance: 20
     * 분당선: X
     */
    @ParameterizedTest
    @MethodSource("provideLine")
    void 구간이_속해있는_노선을_검사한다(Line line, boolean expected) {
        boolean result = 강남역_선릉역_구간.isSameLine(line);
        assertThat(result).isEqualTo(expected);
    }

    private static Stream<Arguments> provideLine() {
        return Stream.of(
                Arguments.of(신분당선, true),
                Arguments.of(분당선, false)
        );
    }

    /**
     * 지하철역: 강남역, 선릉역, 양재역
     * 신분당선: 강남 - 선릉 (10)
     * total distance: 20
     * 분당선: X
     */
    @ParameterizedTest
    @MethodSource("provideUpStation")
    void 구간이_속해있는_상행역을_검사한다(Station station, boolean expected) {
        boolean result = 강남역_선릉역_구간.isUpStation(station);
        assertThat(result).isEqualTo(expected);
    }

    private static Stream<Arguments> provideUpStation() {
        return Stream.of(
                Arguments.of(강남역, true),
                Arguments.of(선릉역, false)
        );
    }

    /**
     * 지하철역: 강남역, 선릉역, 양재역
     * 신분당선: 강남 - 선릉 (10)
     * total distance: 20
     * 분당선: X
     */
    @ParameterizedTest
    @MethodSource("provideDownStation")
    void 구간이_속해있는_하행역을_검사한다(Station station, boolean expected) {
        boolean result = 강남역_선릉역_구간.isDownStation(station);
        assertThat(result).isEqualTo(expected);
    }

    private static Stream<Arguments> provideDownStation() {
        return Stream.of(
                Arguments.of(강남역, false),
                Arguments.of(선릉역, true)
        );
    }

    /**
     * 지하철역: 강남역, 선릉역, 양재역
     * 신분당선: 강남 - 선릉 (10)
     * total distance: 20
     * 분당선: X
     */
    @Test
    void 구간이_속해있는_상행역과_하행역을_조회한다() {
        assertThat(강남역_선릉역_구간.stations()).hasSize(2)
                .containsExactly(강남역, 선릉역);
    }

    /**
     * 지하철역: 강남역, 선릉역, 양재역
     * 신분당선: 강남 - 선릉 (10)
     * total distance: 20
     * 분당선: X
     */
    @Test
    void 구간의_상행역과_노선의_거리를_변경한다() {
        Section 구간 = new Section(
                신분당선,
                강남역,
                선릉역,
                10L
        );
        구간.changeUpStation(양재역, 5L);
        assertAll(
                () -> assertThat(구간.upStation()).isEqualTo(양재역),
                () -> assertThat(구간.distance()).isEqualTo(5L)
        );
    }

    /**
     * 지하철역: 강남역, 선릉역, 양재역
     * 신분당선: 강남 - 선릉 (10)
     * total distance: 20
     * 분당선: X
     */
    @Test
    void 구간의_하행역과_노선의_거리를_변경한다() {
        Section 구간 = new Section(
                신분당선,
                강남역,
                선릉역,
                10L
        );
        구간.changeDownStation(양재역, 5L);
        assertAll(
                () -> assertThat(구간.downStation()).isEqualTo(양재역),
                () -> assertThat(구간.distance()).isEqualTo(5L)
        );
    }

}
