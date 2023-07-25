package nextstep.subway.unit;

import nextstep.subway.line.entity.Line;
import nextstep.subway.section.entity.Section;
import nextstep.subway.section.entity.Sections;
import nextstep.subway.station.entity.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
        assertThat(line.getStations()).hasSize(3)
                .extracting("name")
                .containsExactly(
                        "당고개역", "사당역", "이수역"
                );
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
        assertThat(line.getStations()).hasSize(3)
                .extracting("name")
                .containsExactly(
                        "사당역", "당고개역", "이수역"
                );
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
        assertThat(line.getStations()).hasSize(3)
                .extracting("name")
                .containsExactly(
                        "당고개역", "이수역", "사당역"
                );
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
                .distance(10)
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