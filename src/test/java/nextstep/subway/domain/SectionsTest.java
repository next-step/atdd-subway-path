package nextstep.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionsTest {
    private Line 신분당선;
    private Station 강남역;
    private Station 정자역;
    private int distance;

    @BeforeEach
    void setUp() {
        신분당선 = new Line("신분당선", "red");
        강남역 = new Station("강남역");
        정자역 = new Station("정자역");
        distance = 10;
    }

    @DisplayName("새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void addSection() {
        // given
        신분당선.addSection(강남역, 정자역, distance);
        Station 신논현역 = new Station("신논현역");

        // when
        신분당선.addSection(신논현역, 강남역, distance);

        // then
        assertThat(신분당선.getStations()).containsExactly(신논현역, 강남역, 정자역);
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우")
    @Test
    void addSection2() {
        // given
        신분당선.addSection(강남역, 정자역, distance);
        Station 중간역 = new Station("중간역");

        // when
        신분당선.addSection(강남역, 중간역, 6);

        // then
        assertThat(신분당선.getStations()).containsExactly(강남역, 중간역, 정자역);
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 실패 - 신규 역 사이 길이가 기존 역 사이 길이보다 길거나 같으면 실패")
    @Test
    void addSectionFail() {
        // given
        신분당선.addSection(강남역, 정자역, distance);
        Station 중간역 = new Station("중간역");

        // when & then
        assertThatThrownBy(() -> 신분당선.addSection(강남역, 중간역, distance))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> 신분당선.addSection(중간역, 정자역, distance))
                .isInstanceOf(IllegalArgumentException.class);
    }
}