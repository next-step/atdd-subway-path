package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.exception.EntityAlreadyExistsException;
import nextstep.subway.exception.exception.EntityCannotRemoveException;
import nextstep.subway.exception.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SectionsTest {
    private Line 칠호선;
    private Station 상도역;
    private Station 신대방삼거리역;

    @BeforeEach
    void setUp() {
        // given
        칠호선 = new Line("7호선", "green darken-2");
        상도역 = new Station("상도역");
        신대방삼거리역 = new Station("신대방삼거리역");
        칠호선.addSection(상도역, 신대방삼거리역, 6);
    }

    @Test
    @DisplayName("기존 구간 길이보다 크거나 같은 역 사이 새로운 역을 갖는 구간 등록")
    void addSection_moreThenDistance() {
        // given
        Station 장승배기역 = new Station("장승배기역");

        // when
        // then
        assertAll(
                () -> assertThatThrownBy(() -> 칠호선.addSection(상도역, 장승배기역, 10))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("The length of the section you want to register is greater than or equal to the length of the existing section"),
                () -> assertThatThrownBy(() -> 칠호선.addSection(상도역, 장승배기역, 6))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("The length of the section you want to register is greater than or equal to the length of the existing section")
        );
    }

    @Test
    @DisplayName("상하행역 모두 이미 등록된 구간")
    void addSection_alreadyEnrollStation() {
        // given
        Station 장승배기역 = new Station("장승배기역");
        칠호선.addSection(상도역, 장승배기역, 2);

        // when
        // then
        assertAll(
                () -> assertThatThrownBy(() -> 칠호선.addSection(상도역, 장승배기역, 2))
                        .isInstanceOf(EntityAlreadyExistsException.class)
                        .hasMessage("Both stations already exist on the line."),
                () -> assertThatThrownBy(() -> 칠호선.addSection(상도역, 신대방삼거리역, 6))
                        .isInstanceOf(EntityAlreadyExistsException.class)
                        .hasMessage("Both stations already exist on the line.")
        );

    }

    @Test
    @DisplayName("상하행역 모두 등록되지 않은 구간")
    void addSection_notEnrollStation() {
        // given
        Station 숭실대입구역 = new Station("숭실대입구역");
        Station 장승배기역 = new Station("장승배기역");

        // when
        // then
        assertThatThrownBy(() -> 칠호선.addSection(숭실대입구역, 장승배기역, 6))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("기존 구간 사이 역을 갖는 구간 등록")
    void addSection_middleStation() {
        // given
        Station 장승배기역 = new Station("장승배기역");

        // when
        칠호선.addSection(상도역, 장승배기역, 3);

        // then
        assertThat(칠호선.getStations()).extracting("name").containsExactly("상도역", "장승배기역", "신대방삼거리역");
    }

    @Test
    @DisplayName("상행 종점역을 하행역으로 갖는 구간 등록")
    void addSection_frontSection() {
        // given
        Station 숭실대입구역 = new Station("숭실대입구역");

        // when
        칠호선.addSection(숭실대입구역, 상도역, 3);

        // then
        assertThat(칠호선.getStations()).extracting("name").containsExactly("숭실대입구역", "상도역", "신대방삼거리역");
    }

    @Test
    @DisplayName("하행 종점역을 상행역으로 갖는 구간 등록")
    void addSection_lastSection() {
        // given
        Station 보라매역 = new Station("보라매역");

        // when
        칠호선.addSection(신대방삼거리역, 보라매역, 10);

        // then
        assertThat(칠호선.getStations()).extracting("name").containsExactly("상도역", "신대방삼거리역", "보라매역");
    }

    @Test
    @DisplayName("등록된 구간이 하나 이하인 노선을 삭제")
    void removeSection_lessThanOneSection() {
        // when
        // then
        assertThatThrownBy(() -> 칠호선.removeSection(신대방삼거리역))
                .isInstanceOf(EntityCannotRemoveException.class)
                .hasMessage("If there is less than one registered section, you cannot delete it.");
    }

    @Test
    @DisplayName("노선에 등록되지 않은 구간 삭제")
    void removeSection_notFoundStation() {
        // given
        Station 보라매역 = new Station("보라매역");
        Station 건대입구역 = new Station("건대입구역");
        칠호선.addSection(신대방삼거리역, 보라매역, 5);

        // when
        // then
        assertThatThrownBy(() -> 칠호선.removeSection(건대입구역))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Unregistered Station does not exist.");
    }

    @Test
    @DisplayName("하행 종점역 삭제")
    void removeSection_WithLastEndStation() {
        // given
        Station 보라매역 = new Station("보라매역");
        칠호선.addSection(신대방삼거리역, 보라매역, 5);

        // when
        칠호선.removeSection(신대방삼거리역);

        // then
        assertThat(칠호선.getStations()).extracting("name").containsExactly("상도역", "보라매역");
    }

    @Test
    @DisplayName("상행 종점역 삭제")
    void removeSection_WithFirstEndStation() {
        // given
        Station 보라매역 = new Station("보라매역");
        칠호선.addSection(신대방삼거리역, 보라매역, 5);

        // when
        칠호선.removeSection(상도역);

        // then
        assertThat(칠호선.getStations()).extracting("name").containsExactly("신대방삼거리역", "보라매역");
    }

    @Test
    @DisplayName("중간역 삭제")
    void removeSection_WithMiddleStation() {
        // given
        Station 보라매역 = new Station("보라매역");
        칠호선.addSection(신대방삼거리역, 보라매역, 5);

        // when
        칠호선.removeSection(신대방삼거리역);

        // then
        assertThat(칠호선.getStations()).extracting("name").containsExactly("상도역", "보라매역");
    }
}
