package nextstep.subway.domain;

import nextstep.subway.domain.exception.IllegalDistanceSectionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionTest {

    private final static Line 신분당선 = new Line("신분당선", "red");
    private final static Station 강남역 = new Station("강남역");
    private final static Station 양재역 = new Station("양재역");
    private final static Section givenSection = new Section(신분당선, 강남역, 양재역, 10);

    // When 기존 구간의 상행역 변경 시
    // Then 변경이 된다.
    @DisplayName("구간의 상행역을 변경 할 수 있다")
    @Test
    void 구간의_상행역을_변경_할_수_있다() {
        // When
        Station 신사역 = new Station("신사역");
        givenSection.changeUpStation(신사역, 5);

        // Then
        assertThat(givenSection.getDistance()).isEqualTo(5);
        assertThat(givenSection.getUpStation()).isEqualTo(신사역);
    }

    // When 상행역 변경시 기존 역과 역사이의 길이보다 크거나 같으면
    // Then 변경이 안된다.
    @DisplayName("상행역 변경시 기존 역과 역사이의 길이보다 크거나 같으면 변경이 안된다")
    @Test
    void 상행역_변경시_기존_역과_역사이의_길이보다_크거나_같으면_변경이_안된다() {
        // When && Then
        Station 신사역 = new Station("신사역");
        assertThatThrownBy(() -> givenSection.changeUpStation(신사역, 10)).isInstanceOf(IllegalDistanceSectionException.class);
    }

}