package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LineTest {

    private static final String DONONGSTATIONNAME = "도농역";
    private static final String GOORISTATIONNAME = "구리역";
    private static final String DUCKSOSTATIONNAME = "덕소역";

    private static final String FIRSTLINENAME = "1호선";

    private static final String BLUE = "blue";

    private Station donongStation;
    private Station gooriStation;
    private Station ducksoStation;

    private Line line;

    private Section firstSection;
    private Section secondSection;

    @BeforeEach
    void setup() {
        donongStation = new Station(DONONGSTATIONNAME);
        gooriStation = new Station(GOORISTATIONNAME);
        ducksoStation = new Station(DUCKSOSTATIONNAME);

        line = new Line(FIRSTLINENAME, BLUE);

        firstSection = new Section(line, donongStation, gooriStation, 10);
        secondSection = new Section(line, donongStation, ducksoStation, 5);
    }

    @Test
    void 구간_추가() {
        구간_추가(List.of(firstSection));

        assertThat(line.getLastSection()).isEqualTo(firstSection);
    }

    @Test
    void 모든_구간_조회() {
        assertThat(line.getSections().getAllStation()).containsExactlyElementsOf(List.of());
    }

    @Test
    void 구간_1개_등록_조회() {
        구간_추가(List.of(firstSection));

        assertThat(line.getLastSection()).isEqualTo(firstSection);
        assertThat(line.getSections().getAllStation()).containsExactlyElementsOf(Arrays.asList(donongStation, gooriStation));
    }

    @Test
    void 구간_2개_등록_조회() {
        구간_추가(List.of(firstSection, secondSection));

        assertThat(line.getLastSection()).isEqualTo(secondSection);
        assertThat(line.getSections().getAllStation()).containsExactlyElementsOf(Arrays.asList(donongStation, gooriStation, ducksoStation));
    }

    @Test
    void 구간_삭제() {
        구간_추가(List.of(firstSection, secondSection));

        line.getSections().removeLastSection();

        assertThat(line.getLastSection()).isEqualTo(firstSection);
        assertThat(line.getSections().getAllStation()).containsExactlyElementsOf(Arrays.asList(donongStation, gooriStation));
    }

    private void 구간_추가(List<Section> sections) {
        sections.forEach(
            section -> line.addSection(section)
        );
    }
}