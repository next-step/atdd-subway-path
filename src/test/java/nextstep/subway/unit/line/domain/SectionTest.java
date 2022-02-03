package nextstep.subway.unit.line.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.line.domain.Distance;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;

@DisplayName("Section 단위 테스트")
class SectionTest {
    private Line line;
    private Section section;

    @BeforeEach
    void setUp() {
        Station upStation = new Station(1L, "상행");
        Station downStation = new Station(2L, "하행");
        Distance distance = new Distance(100);

        this.line = new Line(1L, "노선", "bg-red-500");
        this.section = Section.builder()
            .id(1L)
            .line(line)
            .upStation(upStation)
            .downStation(downStation)
            .distance(distance)
            .build();
    }

    @DisplayName("구간의 상행 ID Match")
    @Test
    void matchUpStation() {
        assertThat(section.matchUpStation(section)).isTrue();
    }

    @DisplayName("구간의 하행 ID Match")
    @Test
    void matchDownStation() {
        assertThat(section.matchDownStation(2L)).isTrue();
    }

    @DisplayName("상행을 다른 구간의 하행으로 변경")
    @Test
    void changeUpStation() {
        Station newUpStation = new Station(3L, "새로운 상행");
        Station newDownStation = new Station(4L, "새로운 하행");
        Section newSection = Section.builder()
            .id(2L)
            .line(line)
            .upStation(newUpStation)
            .downStation(newDownStation)
            .distance(new Distance(30))
            .build();

        int expectChangedDistance = section.getDistance().getValue() - newSection.getDistance().getValue();
        section.changeUpStation(newSection);

        assertThat(section.getDistance().getValue()).isEqualTo(expectChangedDistance);
        assertThat(section.getUpStation().getName()).isEqualTo(newDownStation.getName());
    }

    @DisplayName("상행 구간을 합친다.")
    @Test
    void combineUpSection() {
        Station newUpStation = new Station(3L, "제거될 상행");
        Station newDownStation = new Station(4L, "마지막 상행");
        Section newSection = Section.builder()
            .id(2L)
            .line(line)
            .upStation(newUpStation)
            .downStation(newDownStation)
            .distance(new Distance(30))
            .build();

        int expectChangedDistance = section.getDistance().getValue() + newSection.getDistance().getValue();
        section.combineOfUpSection(newSection);

        assertThat(section.getDistance().getValue()).isEqualTo(expectChangedDistance);
        assertThat(section.getUpStation().getName()).isEqualTo(newUpStation.getName());
    }
}
