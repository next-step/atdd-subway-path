package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {

    @Test
    @DisplayName("구간 추가 정상 동작")
    void addSection() {
        //given
        Line line = createLine();
        Section section = createSection(line,"염창역","당산역",10);

        //when
        line.getSections().add(section);

        //then
        assertThat(line.getSections()).containsExactly(section);
    }

    @Test
    @DisplayName("노선 역 호출 정상 동작")
    void getStations() {
        //given
        Line line = createLine();
        Section section = createSection(line,"염창역","당산역",10);
        line.getSections().add(section);

        //when
        List<Section> sections = line.getSections();

        //then
        assertThat(sections).hasSize(1);
        assertThat(sections.get(0).getUpStation().getName()).isEqualTo("염창역");
        assertThat(sections.get(0).getDownStation().getName()).isEqualTo("당산역");
        assertThat(sections.get(0).getDistance()).isEqualTo(10);
    }

    @Test
    @DisplayName("노선 구간 삭제 정상 동작")
    void removeSection() {
        //given
        Line line = createLine();
        Section section = createSection(line,"염창역","당산역",10);
        Section section2 = createSection(line,"당산역","여의도역",10);

        line.getSections().add(section);
        line.getSections().add(section2);

        //when
        line.getSections().removeIf(v->v.getUpStation().getName().equals("염창역"));
        List<String> stationNames = getStationNames(line);

        //then
        assertThat(stationNames).doesNotContain("염창역");
        assertThat(stationNames).hasSize(1);
    }

    private List<String> getStationNames(Line line) {
        return line.getSections().stream()
            .map(v->v.getUpStation().getName())
            .collect(Collectors.toList());
    }

    private Section createSection(Line line,String upStationName,String downStationName,int distance) {
        Station yumChangStation = new Station(upStationName);
        Station dangSanStation = new Station(downStationName);
        return new Section(line, yumChangStation, dangSanStation, distance);
    }

    private Line createLine() {
        return new Line("9호선","YELLOW");
    }

}
