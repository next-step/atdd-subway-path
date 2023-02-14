package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.common.constants.ErrorConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SectionsTest {

    Line 신분당선;
    Station 강남역;
    Station 양재역;
    Section 강남_양재_구간;

    @BeforeEach
    void setUp() {
        // given
        신분당선 = new Line("신분당선", "RED");
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        강남_양재_구간 = new Section(신분당선, 강남역, 양재역, 10);
        신분당선.addSection(강남_양재_구간);
    }

    @Test
    @DisplayName("구간 등록 실패-기존 구간 길이보다 크거나 같은 역 사이 새로운 역을 갖는 구간")
    void addSection_moreThenDistance() {
        // given
        Station 정자역 = new Station("정자역");
        Section 강남_정자_구간 = new Section(신분당선, 강남역, 정자역, 10);
        Section 정자_양재_구간 = new Section(신분당선, 정자역, 양재역, 10);

        // when
        // then
        assertAll(
                () -> assertThatThrownBy(() -> 신분당선.addSection(강남_정자_구간))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage(MORE_THEN_DISTANCE),
                () -> assertThatThrownBy(() -> 신분당선.addSection(정자_양재_구간))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage(MORE_THEN_DISTANCE)
        );
    }

    @Test
    @DisplayName("구간 등록 실패-상행역과 하행역 모두 이미 등록된 구간")
    void addSection_alreadyEnrollStation() {
        // given
        Station 정자역 = new Station("정자역");
        Section 양재_정자_구간 = new Section(신분당선, 양재역, 정자역, 6);
        신분당선.addSection(양재_정자_구간);

        Section 강남_정자_구간 = new Section(신분당선, 강남역, 정자역, 6);

        // when
        // then
        assertAll(
                () -> assertThatThrownBy(() -> 신분당선.addSection(강남_양재_구간))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage(ALREADY_ENROLL_STATION),
                () -> assertThatThrownBy(() -> 신분당선.addSection(강남_정자_구간))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage(ALREADY_ENROLL_STATION)
        );

    }

    @Test
    @DisplayName("구간 등록 실패-상행역과 하행역 둘 중 하나도 등록되지 않은 구간")
    void addSection_notEnrollStation() {
        // given
        Station 정자역 = new Station("정자역");
        Station 판교역 = new Station("판교역");
        Section 정자_판교_구간 = new Section(신분당선, 정자역, 판교역, 6);

        // when
        // then
        assertThatThrownBy(() -> 신분당선.addSection(정자_판교_구간))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(NOT_ENROLL_STATION);
    }

    @Test
    @DisplayName("기존 구간 사이 역을 갖는 구간 등록")
    void addSection_middleStation() {
        // given
        Station 정자역 = new Station("정자역");
        Section 강남_정자_구간 = new Section(신분당선, 강남역, 정자역, 6);

        // when
        신분당선.addSection(강남_정자_구간);

        // then
        assertThat(신분당선.getStations()).extracting("name").containsExactly("강남역", "정자역", "양재역");
    }

    @Test
    @DisplayName("상행 종점역을 하행역으로 갖는 구간 등록")
    void addSection_frontSection() {
        // given
        Station 신논현역 = new Station("신논현역");
        Section 신논현_강남_구간 = new Section(신분당선, 신논현역, 강남역, 6);

        // when
        신분당선.addSection(신논현_강남_구간);

        // then
        assertThat(신분당선.getStations()).extracting("name").containsExactly("신논현역", "강남역", "양재역");
    }

    @Test
    @DisplayName("하행 종점역을 상행역으로 갖는 구간 등록")
    void addSection_lastSection() {
        // given
        Station 판교역 = new Station("판교역");
        Section 양재_판교_구간 = new Section(신분당선, 양재역, 판교역, 6);

        // when
        신분당선.addSection(양재_판교_구간);

        // then
        assertThat(신분당선.getStations()).extracting("name").containsExactly("강남역", "양재역", "판교역");
    }

    @Test
    @DisplayName("구간 삭제 실패-등록된 구간이 하나 이하인 노선")
    void removeSection_lessThanOneSection() {
        // when
        // then
        assertThatThrownBy(() -> 신분당선.removeSection(양재역))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(LESS_THAN_ONE_SECTION);
    }
}
