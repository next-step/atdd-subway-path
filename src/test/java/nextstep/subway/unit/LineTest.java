package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.LineMinimumSectionException;
import nextstep.subway.exception.SectionAlreadyCreateStationException;
import nextstep.subway.exception.SectionDoesNotHaveAlreadyCreateStationException;
import nextstep.subway.exception.SectionInsertDistanceTooLargeException;
import nextstep.subway.fixture.LineFixture;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static nextstep.subway.fixture.SectionFixture.강남_양재_구간;
import static nextstep.subway.fixture.SectionFixture.양재_양재시민의숲_구간;
import static nextstep.subway.fixture.StationFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineTest {
    public static Line 신분당선;
    private int distance = 10;

    @BeforeEach
    public void setUp() {
        신분당선 = new Line(LineFixture.신분당선.getName(), LineFixture.신분당선.getColor());
    }

    @Test
    @DisplayName("Line에 section이 비어있을 때 section추가 테스트")
    void addSectionForLineHasNoSection() {
        // given

        // when
        신분당선.addSection(강남_양재_구간);

        // then
        노선에_구간이_포함되는지_검증(신분당선, List.of(강남_양재_구간));
    }

    @Test
    @DisplayName("Line에 section이 존재할 때 section추가 테스트")
    void addSectionForLineHasSection() {
        // given
        신분당선.addSection(강남_양재_구간);

        // when
        신분당선.addSection(양재_양재시민의숲_구간);

        // then
        노선에_구간이_포함되는지_검증(신분당선, List.of(강남_양재_구간, 양재_양재시민의숲_구간));
    }

    @Test
    @DisplayName("Line의 상행역과 하행역이 모두 등록되어 있다면 예외 테스트")
    void failWhenLineDownStationDiffSectionUpStation() {
        // given
        신분당선.addSection(강남_양재_구간);
        신분당선.addSection(양재_양재시민의숲_구간);

        // when, then
        Section 강남_양재시민의숲_구간 = new Section(신분당선, 강남역, 양재시민의숲역, distance);
        노선에_구간의역이_모두_등록되어있다면_예외(신분당선, 강남_양재시민의숲_구간);
    }

    @Test
    @DisplayName("Line의 역과 Section의 역이 일치하는 것이 없다면 예외")
    void failWhenLineDoesNotContainAnyStation() {
        // given
        신분당선.addSection(강남_양재_구간);

        Station 양재시민의숲역 = new Station("양재시민의숲역");
        Station 미금역 = new Station("미금역");

        Section 등록안된_역_구간 = new Section(신분당선, 양재시민의숲역, 미금역, distance);
        injectId(등록안된_역_구간, 3L);

        // when, then
        노선에_구간의역이_존재하지않는다면_예외(신분당선, 등록안된_역_구간);
    }

    @Test
    void getStations() {
        // given
        신분당선.getSections().add(강남_양재_구간);

        // when
        List<Station> 신분당선_지하철 = 신분당선.getStations();

        // then
        assertThat(신분당선_지하철).containsExactlyElementsOf(List.of(강남역, 양재역));
    }

    @Test
    @DisplayName("구간이 하나인 노선에서 마지막 구간을 제거할 때 예외발생")
    void removeSectionLineHasOneSection() {
        // given
        신분당선.getSections().add(강남_양재_구간);

        // then
        assertThatThrownBy(() -> 신분당선.removeSection(양재역))
                .isInstanceOf(LineMinimumSectionException.class);
    }

    @Test
    @DisplayName("Section을 하나이상 가지고 있는 Line 삭제 테스트 (종점삭제)")
    void removeLastSection() {
        // given
        신분당선.addSection(강남_양재_구간);
        신분당선.addSection(양재_양재시민의숲_구간);

        // when
        신분당선.removeSection(양재시민의숲역);

        // then
        List<Station> 신분당선_지하철 = 신분당선.getStations();
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(신분당선.getSections()).containsExactlyElementsOf(List.of(강남_양재_구간));
            softAssertions.assertThat(신분당선_지하철).containsExactlyElementsOf(List.of(강남역, 양재역));
        });
    }

    @Test
    @DisplayName("Section을 하나이상 가지고 있는 Line 삭제 테스트 (기점삭제)")
    void removeFirstSection() {
        // given
        신분당선.addSection(강남_양재_구간);
        신분당선.addSection(양재_양재시민의숲_구간);

        // when
        신분당선.removeSection(강남역);

        // then
        List<Station> 신분당선_지하철 = 신분당선.getStations();
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(신분당선.getSections()).containsExactlyElementsOf(List.of(양재_양재시민의숲_구간));
            softAssertions.assertThat(신분당선_지하철).containsExactlyElementsOf(List.of(양재역, 양재시민의숲역));
        });
    }

    @Test
    @DisplayName("노선이 가지고 있는 중간구간 삭제 테스트")
    void removeMidSection() {
        // given
        신분당선.addSection(강남_양재_구간);
        신분당선.addSection(양재_양재시민의숲_구간);

        // when
        신분당선.removeSection(양재역);

        // then
        List<Station> 신분당선_지하철 = 신분당선.getStations();
        SoftAssertions.assertSoftly(sa -> {
            sa.assertThat(신분당선.getSections().get(0).getDistance()).isEqualTo(강남_양재_구간.getDistance() + 양재_양재시민의숲_구간.getDistance());
            sa.assertThat(신분당선_지하철).containsExactlyElementsOf(List.of(강남역, 양재시민의숲역));
        });
    }

    @Test
    @DisplayName("Line 정보 변경 테스트")
    void changeLineTest() {
        // given

        // when
        신분당선.changeNameAndColor("새로운노선명", "새로운컬러명");

        // then
        assertThat(신분당선.getName()).isEqualTo("새로운노선명");
        assertThat(신분당선.getColor()).isEqualTo("새로운컬러명");
    }

    @Test
    @DisplayName("노선 내 새 구간 위치 확인")
    void checkNewSectionPositionInLine() {
        // given
        신분당선.addSection(강남_양재_구간);


        Station 노원역 = new Station("노원역");
        // when
        boolean result = 신분당선.checkExistStation(노원역);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("새 구간의 거리가 기존구간보다 크면 예외 발생")
    void insertStationMiddle() {
        // given
        Station 신논현역 = new Station("신논현역");
        injectId(신논현역, 10L);
        Section 신논현_양재_구간 = new Section(신분당선, 신논현역, 양재역, distance);
        신분당선.addSection(신논현_양재_구간);

        // when
        // then
        새구간의_거리가_기존구간보다_크면_예외(신분당선, 강남_양재_구간);
    }

    private void 노선에_구간이_포함되는지_검증(Line 노선, List<Section> 구간들) {
        assertThat(노선.getSections()).containsExactlyElementsOf(구간들);
    }

    private void 노선에_구간의역이_모두_등록되어있다면_예외(Line 노선, Section 구간) {
        assertThatThrownBy(() -> 노선.addSection(구간))
                .isInstanceOf(SectionAlreadyCreateStationException.class);
    }

    private void 노선에_구간의역이_존재하지않는다면_예외(Line 노선, Section 구간) {
        assertThatThrownBy(() -> 노선.addSection(구간))
                .isInstanceOf(SectionDoesNotHaveAlreadyCreateStationException.class);
    }

    private void 새구간의_거리가_기존구간보다_크면_예외(Line 노선, Section 구간) {
        assertThatThrownBy(() -> 노선.addSection(구간))
                .isInstanceOf(SectionInsertDistanceTooLargeException.class);
    }

    private static <T> void injectId(Object o, long id) {
        try {
            Field idField = o.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(o, id);
        } catch (Exception e) {
            throw new IllegalStateException("private필드 id값 inject중 예외발생", e);
        }
    }
}
