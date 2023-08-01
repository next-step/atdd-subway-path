package nextstep.subway.unit;

import nextstep.subway.line.entity.Line;
import nextstep.subway.section.entity.Section;
import nextstep.subway.section.entity.Sections;
import nextstep.subway.station.entity.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SectionsTest {

    private Station 당고개역, 이수역, 사당역;

    @BeforeEach
    void setUpStation() {
        당고개역 = createStation("당고개역");
        이수역 = createStation("이수역");
        사당역 = createStation("사당역");
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 확인")
    @Test
    void isUptoUp() {
        // given : 선행조건 기술
        Line line = line(당고개역, 이수역);
        Section section = section(line, 당고개역, 사당역, 3);
        Sections sections = line.getSections();

        // when : 기능 수행
        boolean isUptoUp = sections.isUptoUp(section);

        // then : 결과 확인
        assertThat(isUptoUp).isTrue();
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우")
    @Test
    void addNewStationBetweenExistingStation() {
        // given : 선행조건 기술
        Line line = line(당고개역, 이수역);
        Section section = section(line, 당고개역, 사당역, 3);
        Sections sections = line.getSections();

        // when : 기능 수행
        sections.addNewStationBetweenExistingStation(section, line);

        // then : 결과 확인
        역_목록_검증(line, line.getStations().size(), Arrays.asList("당고개역", "사당역", "이수역"));
    }

    @DisplayName("새로운 역을 상행 종점으로 등록할 경우 확인")
    @Test
    void isUpToDown() {
        // given : 선행조건 기술
        Line line = line(당고개역, 이수역);
        Section section = section(line, 사당역, 당고개역, 3);
        Sections sections = line.getSections();

        // when : 기능 수행
        boolean isUpToDown = sections.isUpToDown(section);

        // then : 결과 확인
        assertThat(isUpToDown).isTrue();
    }

    @DisplayName("새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void addNewStationAsAnUpStation() {
        // given : 선행조건 기술
        Line line = line(당고개역, 이수역);
        Section section = section(line, 사당역, 당고개역, 3);
        Sections sections = line.getSections();

        // when : 기능 수행
        sections.addNewStationAsAnUpStation(section);

        // then : 결과 확인
        역_목록_검증(line, line.getStations().size(), Arrays.asList("사당역", "당고개역", "이수역"));
    }

    @DisplayName("새로운 역을 하행 종점으로 등록할 경우 확인")
    @Test
    void isDownToUp() {
        // given : 선행조건 기술
        Line line = line(당고개역, 이수역);
        Section section = section(line, 이수역, 사당역, 3);
        Sections sections = line.getSections();

        // when : 기능 수행
        boolean isDownToUp = sections.isDownToUp(section);

        // then : 결과 확인
        assertThat(isDownToUp).isTrue();
    }

    @DisplayName("새로운 역을 하행 종점으로 등록할 경우")
    @Test
    void addNewStationAsAnDownStation() {
        // given : 선행조건 기술
        Line line = line(당고개역, 이수역);
        Section section = section(line, 이수역, 사당역, 3);
        Sections sections = line.getSections();

        // when : 기능 수행
        sections.addNewStationAsAnDownStation(section);

        // then : 결과 확인
        역_목록_검증(line, line.getStations().size(), Arrays.asList("당고개역", "이수역", "사당역"));
    }

    @DisplayName("구간 제거 - 첫번째 역을 제거 했을 경우")
    @Test
    void removeSection1() {
        // given : 선행조건 기술
        Line line = line(당고개역, 이수역);
        Section section = section(line, 이수역, 사당역, 10);
        Sections sections = line.getSections();
        sections.addNewStationAsAnDownStation(section);

        // when : 기능 수행
        sections.removeSection(당고개역);

        // then : 결과 확인
        역_목록_검증(line, line.getStations().size(), Arrays.asList("이수역", "사당역"));
        구간의_거리의_합_확인(line, sections);
    }

    @DisplayName("구간 제거 - 중간 역을 제거 했을 경우")
    @Test
    void removeSection2() {
        // given : 선행조건 기술
        Line line = line(당고개역, 이수역);
        Section section = section(line, 이수역, 사당역, 10);
        Sections sections = line.getSections();
        sections.addNewStationAsAnDownStation(section);

        // when : 기능 수행
        sections.removeSection(이수역);

        // then : 결과 확인
        역_목록_검증(line, line.getStations().size(), Arrays.asList("당고개역", "사당역"));
        구간의_거리의_합_확인(line, line.getSections());
    }

    @DisplayName("구간 제거 - 마지막 역을 제거 했을 경우")
    @Test
    void removeSection3() {
        // given : 선행조건 기술
        Line line = line(당고개역, 이수역);
        Section section = section(line, 이수역, 사당역, 10);
        Sections sections = line.getSections();
        sections.addNewStationAsAnDownStation(section);

        // when : 기능 수행
        sections.removeSection(사당역);

        // then : 결과 확인
        역_목록_검증(line, line.getStations().size(), Arrays.asList("당고개역", "이수역"));
        구간의_거리의_합_확인(line, sections);
    }

    private void 역_목록_검증(Line line, int size, List<String> names) {
        assertThat(line.getStations()).hasSize(size)
                .extracting("name")
                .containsExactly(
                        names.toArray()
                );
    }

    private void 구간의_거리의_합_확인(Line line, Sections sections) {
        int sumDistance = sections.getSections().stream()
                .mapToInt(Section::getDistance)
                .sum();

        assertThat(line.getSections().getDistance()).isEqualTo(sumDistance);
    }

    private Station createStation(String stationName) {
        return new Station(stationName);
    }

    private Line line(Station upStation, Station downStation) {
        return Line.builder()
                .name("4호선")
                .color("blue")
                .upStation(upStation)
                .downStation(downStation)
                .section(section(upStation, downStation))
                .build();
    }

    private Section section(Station upStation, Station downStation) {
        return Section.builder()
                .upStation(upStation)
                .downStation(downStation)
                .distance(10)
                .build();
    }

    private Section section(Line line, Station upStation, Station downStation, int distance) {
        return Section.builder()
                .line(line)
                .upStation(upStation)
                .downStation(downStation)
                .distance(distance)
                .build();
    }
}