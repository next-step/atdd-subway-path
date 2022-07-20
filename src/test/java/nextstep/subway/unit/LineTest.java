package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class LineTest {
    private static final long 잠실역_ID = 3L;
    public static final long 강남역_ID = 1L;
    public static final long 역삼역_ID = 2L;
    private static final Station 강남역 = new Station(강남역_ID, "강남역");
    private static final Station 역삼역 = new Station(역삼역_ID, "역삼역");
    private static final Station 잠실역 = new Station(잠실역_ID, "잠실역");
    public static final Section 역삼역_잠실역_구간 = new Section(역삼역, 잠실역, 10);
    private final Section 강남역_역삼역_구간 = new Section(강남역, 역삼역, 10);
    private final Line 분당선 = new Line("분당선", "red", 강남역_역삼역_구간);

    @Test
    @DisplayName("노선(Line)은 구간들(Sections)의 정보와 이름 색상을 가지고 있다.")
    void createSection() {
        // when & then
        assertDoesNotThrow(
            () -> new Line("분당선", "red", 강남역_역삼역_구간)
        );
    }

    @Test
    @DisplayName("노선은 최소 하나 이상의 구간 정보를 포함하고 있다.")
    void createSectionValidationTest1() {
        // when & then
        assertThatThrownBy(
            () -> new Line("분당선", "red", null)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "선")
    @DisplayName("노선의 이름은 비어있거나 null이 될 수 없습니다.")
    void createSectionValidationTest2(String 이름) {
        // when & then
        assertThatThrownBy(
            () -> new Line(이름, "red", 강남역_역삼역_구간)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "c")
    @DisplayName("노선의 색은 비어있거나 null이 될 수 없습니다.")
    void createSectionValidationTest3(String 색) {
        // when & then
        assertThatThrownBy(
            () -> new Line("분당선", 색, 강남역_역삼역_구간)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("구간(Section)을 추가한다.(addSection)")
    void addSection() {
        // given
        Section 역삼역_잠실역_구간 = new Section(역삼역, 잠실역, 10);

        // when & then
        assertDoesNotThrow(
            () -> 분당선.addSection(역삼역_잠실역_구간)
        );
    }

    @Test
    @DisplayName("추가하는 구간의 상행선이 노선의 종점 하행선과 같지 않으면 예외가 발생한다.")
    void addSectionValidationTest1() {
        // given
        Section 잠실역_역삼역_구간 = new Section(잠실역, 역삼역, 10);

        // when & then
        assertThatThrownBy(
            () -> 분당선.addSection(잠실역_역삼역_구간)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("추가하는 하행선이 노선에 포함된 역이 아니어야 한다.")
    void addSectionValidationTest2() {
        // given
        Section 역삼역_잠실역_구간 = new Section(역삼역, 잠실역, 10);
        Section 잠실역_강남역_구간 = new Section(잠실역, 강남역, 10);
        분당선.addSection(역삼역_잠실역_구간);

        // when & then
        assertThatThrownBy(
            () -> 분당선.addSection(잠실역_강남역_구간)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("추가하는 상행선과 하행선이 같으면 예외가 발생한다.")
    void addSectionValidationTest3() {
        // given
        Section 역삼역_잠실역_구간 = new Section(역삼역, 잠실역, 10);
        Section 잠실역_잠실역_구간 = new Section(잠실역, 잠실역, 10);
        분당선.addSection(역삼역_잠실역_구간);

        // when & then
        assertThatThrownBy(
            () -> 분당선.addSection(잠실역_잠실역_구간)
        ).isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    @DisplayName("구간(Sections)을 조회한다.(getSections)")
    void getStations() {
        // when & then
        List<Section> 분당선_구간_조회 = assertDoesNotThrow(
            분당선::getSections
        );
        assertThat(분당선_구간_조회).hasSize(1);
    }


    @Test
    @DisplayName("구간을 추가한 순서대로 정보를 가져와야 한다.")
    void getStationsValidationTest1() {
        // when
        분당선.addSection(역삼역_잠실역_구간);

        // then
        assertThat(분당선.getSections())
            .containsExactly(강남역_역삼역_구간, 역삼역_잠실역_구간);
    }

    @Test
    @DisplayName("구간을 조회할 때 내부 정보를 수정할 수 없어야 한다.")
    void getStationsValidationTest2() {
        // given
        List<Section> 구간_정보 = 분당선.getSections();

        // when
        구간_정보.remove(0);

        // then
        assertThat(분당선.getSections()).hasSize(1);
    }

    @Test
    @DisplayName("구간(Section)을 삭제한다.(deleteSection)")
    void removeSection() {
        // given
        분당선.addSection(역삼역_잠실역_구간);

        // when & then
        assertAll(
            () -> assertDoesNotThrow(() -> 분당선.deleteSection(잠실역_ID)),
            () -> assertThat(분당선.getSections()).hasSize(1)
        );
    }

    @Test
    @DisplayName("노선의 종점 하행선만 삭제할 수 있다.")
    void removeSectionValidationTest1() {
        // given
        분당선.addSection(역삼역_잠실역_구간);

        // when & then
        assertAll(
            () -> assertThatThrownBy(() -> 분당선.deleteSection(강남역_ID)).isInstanceOf(IllegalArgumentException.class),
            () -> assertThat(분당선.getSections()).hasSize(2)
        );
    }

    @Test
    @DisplayName("마지막 남은 구간을 삭제할 수 없다.")
    void removeSectionValidationTest2() {
        // when & then
        assertAll(
            () -> assertThatThrownBy(() -> 분당선.deleteSection(역삼역_ID)).isInstanceOf(IllegalArgumentException.class),
            () -> assertThat(분당선.getSections()).hasSize(1)
        );
    }
}
