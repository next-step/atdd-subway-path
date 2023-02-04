package nextstep.subway.unit;

import nextstep.subway.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        final Line 노선_신분당선 = new Line(1L, 신분당선, 빨간색, new Sections(List.of(첫번째_구간)));

        assertThat(노선_신분당선).isEqualTo(new Line(1L, 신분당선, 빨간색, new Sections(List.of(첫번째_구간))));
    }

    @DisplayName("노선 정보를 수정한다.")
    @Test
    void updateLine() {
        final Section 첫번째_구간 = new Section(강남역, 양재역, 10);
        final Line 노선_신분당선 = new Line(1L, 신분당선, 빨간색, new Sections(List.of(첫번째_구간)));
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
        final Section 첫번째_구간 = new Section(1L, 강남역, 양재역, 10);
        final Line 노선_신분당선 = new Line(1L, 신분당선, 빨간색, new Sections(Arrays.asList(첫번째_구간)));
        노선_신분당선.addSection(양재역, 몽촌토성역, 10);

        final List<Station> 노선_구간_목록 = convertToStation(노선_신분당선.getSectionsList());
        assertAll(
                () -> assertThat(노선_구간_목록).hasSize(3),
                () -> assertThat(노선_구간_목록).contains(강남역, 양재역, 몽촌토성역)
        );
    }

    @DisplayName("노선 구간을 삭제한다.")
    @Test
    void removeSection() {
        final Section 첫번째_구간 = new Section(1L, 강남역, 양재역, 10);
        final Section 두번째_구간 = new Section(2L, 양재역, 몽촌토성역, 10);
        final Line 이호선 = new Line(1L, "2호선", "bg-red-600", new Sections(List.of(첫번째_구간, 두번째_구간)));

        이호선.removeSection(몽촌토성역);

        assertAll(
                () -> assertThat(이호선.getSectionsList()).hasSize(1),
                () -> assertThat(이호선.getSectionsList().get(0)).isEqualTo(첫번째_구간)
        );
    }

    private List<Station> convertToStation(final List<Section> sections) {
        return sections.stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .distinct()
                .collect(Collectors.toList());
    }

    private Distance 거리(final Integer distance) {
        return new Distance(distance);
    }
}
