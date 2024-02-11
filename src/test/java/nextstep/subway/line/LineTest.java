package nextstep.subway.line;

import nextstep.subway.line.section.Section;
import nextstep.subway.line.section.Sections;
import nextstep.subway.station.Station;
import nextstep.subway.testhelper.LineFixture;
import nextstep.subway.testhelper.StationFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LineTest {
    private Line line;
    private Section createdSection;

    private Section inputSection;

    @BeforeEach
    void setUp() {
        createdSection = new Section(StationFixture.강남역,
                StationFixture.선릉역,
                10L);

        line = new Line(LineFixture.신분당선,
                "bg-red-600",
                StationFixture.강남역,
                StationFixture.선릉역,
                10L);

        inputSection = new Section(
                StationFixture.선릉역,
                StationFixture.교대역,
                5L);
    }

    @Test
    @DisplayName("생성된 라인에 시작 구간을 더할 수 있다")
    void addSection1() {
        Section newSection = new Section(
                StationFixture.교대역,
                StationFixture.강남역,
                5L);
        line.addSection(newSection);

        Sections actual = line.getSections();
        Sections expected = Sections.from(
                List.of(newSection,
                        createdSection));
        assertThat(actual).isEqualTo(expected);

        Long actualDistance = line.getDistance();
        Long expectedDistance = 15L;
        assertThat(actualDistance).isEqualTo(expectedDistance);
    }

    @Test
    @DisplayName("생성된 라인에 끝 구간을 더할 수 있다")
    void addSection2() {
        Section newSection = new Section(
                StationFixture.선릉역,
                StationFixture.교대역,
                5L);
        line.addSection(newSection);

        Sections actual = line.getSections();
        Sections expected = Sections.from(
                List.of(createdSection,
                        newSection));
        assertThat(actual).isEqualTo(expected);

        Long actualDistance = line.getDistance();
        Long expectedDistance = 15L;
        assertThat(actualDistance).isEqualTo(expectedDistance);
    }

    @Test
    @DisplayName("생성된 라인에 중간에 구간을 더할 수 있다 (시작점을 기준으로 일치 시")
    void addSection3() {
        Section newSection = new Section(
                StationFixture.강남역,
                StationFixture.교대역,
                5L);
        line.addSection(newSection);

        Sections actual = line.getSections();
        Sections expected = Sections.from(
                List.of(new Section(StationFixture.강남역,
                                StationFixture.교대역,
                                5L),
                        new Section(StationFixture.교대역,
                                StationFixture.선릉역,
                                5L)));
        assertThat(actual).isEqualTo(expected);

        Long actualDistance = line.getDistance();
        Long expectedDistance = 10L;
        assertThat(actualDistance).isEqualTo(expectedDistance);
    }

    @Test
    @DisplayName("생성된 라인에 중간에 구간을 더할 수 있다 (끝점을 기준으로 일치 시")
    void addSection4() {
        Section newSection = new Section(
                StationFixture.교대역,
                StationFixture.선릉역,
                5L);
        line.addSection(newSection);

        Sections actual = line.getSections();
        Sections expected = Sections.from(
                List.of(new Section(StationFixture.강남역,
                                StationFixture.교대역,
                                5L),
                        new Section(StationFixture.교대역,
                                StationFixture.선릉역,
                                5L)));
        assertThat(actual).isEqualTo(expected);

        Long actualDistance = line.getDistance();
        Long expectedDistance = 10L;
        assertThat(actualDistance).isEqualTo(expectedDistance);
    }

    @Test
    @DisplayName("이미 추가된 구간은 추가 할 수 없다")
    void addSection5() {
        assertThrows(IllegalArgumentException.class, () -> line.addSection(createdSection));
    }

    @Test
    @DisplayName("중간에 더하는 구간의 길이는 전체 라인의 길이보다 작아야한다")
    void addSection6() {
        Section newSection = new Section(
                StationFixture.강남역,
                StationFixture.교대역,
                10L);
        assertThrows(IllegalArgumentException.class, () -> line.addSection(newSection));
    }

    @Test
    @DisplayName("등록되어 있는 구간 중 가장 처음 시작 되는 상행역을 삭제하는 경우 할 수 있다.")
    void deleteSection1() {
        line.addSection(inputSection);
        line.deleteSection(StationFixture.강남역);

        Sections actual = line.getSections();
        Sections expected = Sections.from(
                List.of(new Section(StationFixture.선릉역,
                        StationFixture.교대역,
                        5L)));
        assertThat(actual).isEqualTo(expected);

        Long actualDistance = line.getDistance();
        Long expectedDistance = 5L;
        assertThat(actualDistance).isEqualTo(expectedDistance);
    }

    @Test
    @DisplayName("등록되어 있는 구간 중 가장 마지막 하행 되는 역을 삭제 할 수 있다.")
    void deleteSection2() {
        line.addSection(inputSection);
        line.deleteSection(StationFixture.교대역);

        Sections actual = line.getSections();
        Sections expected = Sections.from(
                List.of(new Section(StationFixture.강남역,
                        StationFixture.선릉역,
                        10L)));
        assertThat(actual).isEqualTo(expected);

        Long actualDistance = line.getDistance();
        Long expectedDistance = 10L;
        assertThat(actualDistance).isEqualTo(expectedDistance);
    }

    @Test
    @DisplayName("등록되어 있는 구간 중 중간에 등록되어 있는 역을 삭제 할 수 있다.")
    void deleteSection3() {
        line.addSection(inputSection);
        line.deleteSection(StationFixture.선릉역);

        Sections actual = line.getSections();
        Sections expected = Sections.from(
                List.of(new Section(StationFixture.강남역,
                        StationFixture.교대역,
                        15L)));
        assertThat(actual).isEqualTo(expected);

        Long actualDistance = line.getDistance();
        Long expectedDistance = 15L;
        assertThat(actualDistance).isEqualTo(expectedDistance);
    }

    @Test
    @DisplayName("삭제 되는 역을 찾지 못하는 경우")
    void deleteSection4() {
        assertThrows(IllegalArgumentException.class, () -> line.deleteSection(StationFixture.서초역));
    }

    @ParameterizedTest
    @DisplayName("생성된 라인의 구간이 하나일 때 시작역과 끝역은 삭제 할 수 없다")
    @CsvSource(value = {"1, 강남역", "2, 선릉역"})
    void deleteSection5(Long id,
                        String name) {
        assertThrows(IllegalArgumentException.class, () -> line.deleteSection(new Station(id, name)));
    }

}
