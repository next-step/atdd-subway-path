package nextstep.subway.unit;

import nextstep.subway.line.entity.Line;
import nextstep.subway.section.entity.Section;
import nextstep.subway.station.entity.Station;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {
    @Test
    void addSection() {
        // given : 선행조건 기술
        Line line = line();

        // when : 기능 수행
        line.addSection(section(line));

        // then : 결과 확인
        assertThat(line.getSections().size()).isEqualTo(2);
    }

    @Test
    void getSections() {
        // given : 선행조건 기술
        Line line = line();
        line.addSection(section(line));

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
        Line line = line();
        Section section = section(line);
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


    private Line line() {
        return Line.builder()
                .name("4호선")
                .color("blue")
                .upStation(당고개역())
                .downStation(이수역())
                .distance(10)
                .section(section())
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

    private Section section() {
        return Section.builder()
                .upStation(당고개역())
                .downStation(이수역())
                .distance(10)
                .build();
    }

    private Section section(Line line) {
        return Section.builder()
                .line(line)
                .upStation(이수역())
                .downStation(사당역())
                .distance(10)
                .build();
    }
}
