package nextstep.subway.line;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import nextstep.subway.line.section.Section;
import nextstep.subway.line.section.Sections;
import nextstep.subway.station.Station;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LineTest {
    private static final String 신분당선 = "신분당선";
    private static final String 강남역 = "강남역";
    private static final String 선릉역 = "선릉역";
    private static final String 교대역 = "교대역";
    private static final String 서초역 = "서초역";
    private Line line;
    private Section inputSection;

    @BeforeEach
    void setUp() {
        line = new Line(신분당선,
                "bg-red-600",
                new Station(1L, 강남역),
                new Station(2L, 선릉역),
                10L);

        inputSection = new Section(
                new Station(2L, 선릉역),
                new Station(3L, 교대역),
                5L);
    }

    @Test
    @DisplayName("생성된 라인에 구간을 더할 수 있다")
    void addSection1() {
        line.addSection(inputSection);

        Sections actual = line.getSections();
        Sections expected = Sections.from(
                List.of(new Section(new Station(1L, 강남역),
                                new Station(2L, 선릉역),
                                10L),
                        new Section(new Station(2L, 선릉역),
                                new Station(3L, 교대역),
                                5L)));
        assertThat(actual).isEqualTo(expected);

        Long actualDistance = line.getDistance();
        Long expectedDistance = 15L;
        assertThat(actualDistance).isEqualTo(expectedDistance);
    }

    @Test
    @DisplayName("생성된 라인의 마지막 역과 더하는 구간의 시작역이 다르면 더할 수 없다")
    void addSection2() {
        Section input = new Section(
                new Station(3L, 교대역),
                new Station(4L, 서초역),
                5L);
        assertThrows(IllegalArgumentException.class, () -> line.addSection(input));
    }

    @Test
    @DisplayName("생성된 라인의 역들에 더하는 구간의 마지막역이 포함되어 있으면 더할 수 없다")
    void addSection3() {
        line.addSection(inputSection);

        Section input = new Section(
                new Station(3L, 교대역),
                new Station(2L, 선릉역),
                5L);
        assertThrows(IllegalArgumentException.class, () -> line.addSection(input));
    }

    @Test
    @DisplayName("생성된 라인의 구간을 삭제 할 수 있다.")
    void deleteSection1() {
        line.addSection(inputSection);
        line.deleteSection(new Station(3L, 교대역));

        Sections actual = line.getSections();
        Sections expected = Sections.from(
                List.of(new Section(new Station(1L, 강남역),
                                new Station(2L, 선릉역),
                                10L)));
        assertThat(actual).isEqualTo(expected);

        Long actualDistance = line.getDistance();
        Long expectedDistance = 10L;
        assertThat(actualDistance).isEqualTo(expectedDistance);
    }

    @Test
    @DisplayName("생성된 라인의 마지막 구간이 아니면 삭제가 안된다")
    void deleteSection2() {
        line.addSection(inputSection);
        assertThrows(IllegalArgumentException.class, () -> line.deleteSection(new Station(2L, 선릉역)));
    }

    @Test
    @DisplayName("생성된 라인의 마지막 구간이 2개면 삭제가 안된다")
    void deleteSection3() {
        assertThrows(IllegalArgumentException.class, () -> line.deleteSection(new Station(2L, 선릉역)));
    }

}
