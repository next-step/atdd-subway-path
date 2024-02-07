package nextstep.subway.line;

import nextstep.subway.line.section.Section;
import nextstep.subway.line.section.Sections;
import nextstep.subway.station.Station;
import nextstep.subway.testhelper.LineFixture;
import nextstep.subway.testhelper.StationFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LineTest {
    private Line line;
    private Section createdSection;

    private Section inputSection;

    @BeforeEach
    void setUp() {
        createdSection = new Section(new Station(1L, StationFixture.강남역),
                new Station(2L, StationFixture.선릉역),
                10L);

        line = new Line(LineFixture.신분당선,
                "bg-red-600",
                new Station(1L, StationFixture.강남역),
                new Station(2L, StationFixture.선릉역),
                10L);

        inputSection = new Section(
                new Station(2L, StationFixture.선릉역),
                new Station(3L, StationFixture.교대역),
                5L);
    }

    @Test
    @DisplayName("생성된 라인에 시작 구간을 더할 수 있다")
    void addSection1() {
        Section newSection = new Section(
                new Station(3L, StationFixture.교대역),
                new Station(1L, StationFixture.강남역),
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
                new Station(2L, StationFixture.선릉역),
                new Station(3L, StationFixture.교대역),
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
                new Station(1L, StationFixture.강남역),
                new Station(3L, StationFixture.교대역),
                5L);
        line.addSection(newSection);

        Sections actual = line.getSections();
        Sections expected = Sections.from(
                List.of(new Section(new Station(1L, StationFixture.강남역),
                                new Station(3L, StationFixture.교대역),
                                5L),
                        new Section(new Station(3L, StationFixture.교대역),
                                new Station(2L, StationFixture.선릉역),
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
                new Station(3L, StationFixture.교대역),
                new Station(2L, StationFixture.선릉역),
                5L);
        line.addSection(newSection);

        Sections actual = line.getSections();
        Sections expected = Sections.from(
                List.of(new Section(new Station(1L, StationFixture.강남역),
                                new Station(3L, StationFixture.교대역),
                                5L),
                        new Section(new Station(3L, StationFixture.교대역),
                                new Station(2L, StationFixture.선릉역),
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
                new Station(1L, StationFixture.강남역),
                new Station(3L, StationFixture.교대역),
                10L);
        assertThrows(IllegalArgumentException.class, () -> line.addSection(newSection));
    }

    @Test
    @DisplayName("생성된 라인의 구간을 삭제 할 수 있다.")
    void deleteSection1() {
        line.addSection(inputSection);
        line.deleteSection(new Station(3L, StationFixture.교대역));

        Sections actual = line.getSections();
        Sections expected = Sections.from(
                List.of(new Section(new Station(1L, StationFixture.강남역),
                        new Station(2L, StationFixture.선릉역),
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
        assertThrows(IllegalArgumentException.class, () -> line.deleteSection(new Station(2L, StationFixture.선릉역)));
    }

    @Test
    @DisplayName("생성된 라인의 마지막 구간이 2개면 삭제가 안된다")
    void deleteSection3() {
        assertThrows(IllegalArgumentException.class, () -> line.deleteSection(new Station(2L, StationFixture.선릉역)));
    }

}
