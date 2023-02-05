package nextstep.subway.unit;

import nextstep.subway.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("노선 객체 테스트")
class LineTest {

    private static final String 이호선 = "2호선";
    private static final String 빨간색 = "bg-red-600";
    private static final Station 강남역 = new Station(1L, "강남역");
    private static final Station 양재역 = new Station(2L, "양재역");
    private static final Station 몽촌토성역 = new Station(3L, "몽촌토성역");
    private static final String 녹색 = "bg-green-600";
    private static final String 신분당선 = "신분당선";

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        final Section 첫번째_구간 = new Section(강남역, 양재역, 10);
        final Line 노선_신분당선 = 노선_생성(1L, 신분당선, 빨간색, List.of(첫번째_구간));

        assertThat(노선_신분당선).isEqualTo(노선_생성(1L, 신분당선, 빨간색, List.of(첫번째_구간)));
    }

    @DisplayName("노선 정보를 수정한다.")
    @Test
    void updateLine() {
        final Section 첫번째_구간 = new Section(강남역, 양재역, 10);
        final Line 노선_신분당선 = 노선_생성(1L, 신분당선, 빨간색, List.of(첫번째_구간));
        노선_신분당선.updateLine(이호선, 녹색);

        assertAll(
                () -> assertThat(노선_신분당선.getId()).isEqualTo(1L),
                () -> assertThat(노선_신분당선.getName()).isEqualTo(이호선),
                () -> assertThat(노선_신분당선.getColor()).isEqualTo(녹색)
        );
    }

    @DisplayName("노선의 구간을 추가한다.")
    @Test
    void addSection() {
        final Section 첫번째_구간 = 구간_생성(1L, 강남역, 양재역, 10);
        final Line 노선_신분당선 = 노선_생성(1L, 신분당선, 빨간색, Arrays.asList(첫번째_구간));
        노선_신분당선.addSection(양재역, 몽촌토성역, 10);

        final List<Station> stations = 노선_신분당선.convertToStation();
        assertAll(
                () -> assertThat(stations).hasSize(3),
                () -> assertThat(stations).contains(강남역, 양재역, 몽촌토성역)
        );
    }

    @DisplayName("노선 구간을 삭제한다.")
    @Test
    void removeSection() {
        final Section 첫번째_구간 = 구간_생성(1L, 강남역, 양재역, 10);
        final Section 두번째_구간 = 구간_생성(2L, 양재역, 몽촌토성역, 10);
        final Line 이호선 = 노선_생성(1L, "2호선", "bg-red-600", List.of(첫번째_구간, 두번째_구간));

        이호선.removeSection(몽촌토성역);

        final List<Station> stations = 이호선.convertToStation();
        assertAll(
                () -> assertThat(stations).hasSize(2),
                () -> assertThat(stations).containsExactly(강남역, 양재역)
        );
    }

    private Line 노선_생성(final Long id, final String name, final String color, final List<Section> sections) {
        final Line 노선 = new Line(name, color, new Sections(sections));
        reflectionById(id, 노선);
        return 노선;
    }

    private Section 구간_생성(final Long id, final Station upStation, final Station downStation, final Integer distance) {
        final Section 구간 = new Section(upStation, downStation, distance);
        reflectionById(id, 구간);
        return 구간;
    }

    private void reflectionById(final Long id, final Object object) {
        ReflectionTestUtils.setField(object, "id", id);
    }
}
