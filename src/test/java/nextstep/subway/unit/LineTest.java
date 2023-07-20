package nextstep.subway.unit;

import nextstep.subway.line.entity.Line;
import nextstep.subway.section.entity.Section;
import nextstep.subway.station.entity.Station;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    private Station 당고개역, 이수역, 사당역;

    @BeforeEach
    void setUpStation() {
        당고개역 = 당고개역();
        이수역 = 이수역();
        사당역 = 사당역();
    }

    @DisplayName("새로운 구간을 등록한다. - 새로운 구간의 상행역과 기존 구간의 상행역이 같은 경우")
    @Test
    void addSection() {
        // given : 선행조건 기술
        Line line = line(당고개역, 이수역);

        // when : 기능 수행
        line.addSection(section(line, 당고개역, 사당역));

        // then : 결과 확인
        assertThat(line.getSections()).hasSize(2)
                .extracting("upStation.name", "downStation.name")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("당고개역", "사당역"),
                        Tuple.tuple("사당역", "이수역")
                );
    }

    @DisplayName("새로운 구간을 등록한다. - 새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void addSection2() {
        // given : 선행조건 기술
        Line line = line(당고개역, 이수역);

        // when : 기능 수행
        line.addSection(section(line, 사당역, 당고개역));

        // then : 결과 확인
        assertThat(line.getSections()).hasSize(2)
                .extracting("upStation.name", "downStation.name")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("사당역", "당고개역"),
                        Tuple.tuple("당고개역", "이수역")
                );
    }

    @DisplayName("새로운 구간을 등록한다. - 새로운 역을 하행 종점으로 등록할 경우")
    @Test
    void addSection3() {
        // given : 선행조건 기술
        Line line = line(당고개역, 이수역);

        // when : 기능 수행
        line.addSection(section(line, 이수역, 사당역));

        // then : 결과 확인
        assertThat(line.getSections()).hasSize(2)
                .extracting("upStation.name", "downStation.name")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("당고개역", "이수역"),
                        Tuple.tuple("이수역", "사당역")
                );
    }

    @Test
    void getSections() {
        // given : 선행조건 기술
        Line line = line(당고개역, 이수역);
        line.addSection(section(line, 당고개역, 사당역));

        // when : 기능 수행
        List<Section> sections = line.getSections();

        // then : 결과 확인
        assertThat(sections).hasSize(2)
                .extracting("upStation.name", "downStation.name")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("당고개역", "이수역"),
                        Tuple.tuple("이수역", "사당역")
                );
    }

    @Test
    void removeSection() {
        Line line = line(당고개역, 이수역);
        Section section = section(line, 당고개역, 이수역);
        line.addSection(section);
        Station 사당역 = section.getDownStation();

        // when : 기능 수행
        line.removeSection(사당역);

        // then : 결과 확인
        List<Section> sections = line.getSections();
        assertThat(sections).hasSize(1)
                .extracting("upStation.name", "downStation.name")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("당고개역", "이수역")
                );
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

    private Station 당고개역() {
        return new Station("당고개역");
    }

    private Station 이수역() {
        return new Station("이수역");
    }

    private Station 사당역() {
        return new Station("사당역");
    }

    private Section section(Station upStation, Station downStation) {
        return Section.builder()
                .upStation(upStation)
                .downStation(downStation)
                .distance(10)
                .build();
    }

    private Section section(Line line, Station upStation, Station downStation) {
        return Section.builder()
                .line(line)
                .upStation(upStation)
                .downStation(downStation)
                .distance(3)
                .build();
    }
}
