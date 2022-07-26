package nextstep.subway.unit;

import static nextstep.subway.utils.TestVariables.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {

    private Line line;
    private Station station1;
    private Station station2;
    private Station station3;


    @BeforeEach
    void setUp() {
        line = createLine();
        station1 = new Station(1L,염창역);
        station2 = new Station(2L,당산역);
        station3 = new Station(3L,여의도역);
    }

    @Test
    @DisplayName("구간 추가 정상 동작")
    void addSection() {
        //when
        addSection(line,station1,station2,10);

        //then
        assertThat(line.getLineSection().getSections().get(0).getUpStation().getName()).isEqualTo(염창역);
        assertThat(line.getLineSection().getSections().get(0).getDownStation().getName()).isEqualTo(당산역);
        assertThat(line.getLineSection().getSections().get(0).getDistance()).isEqualTo(10);
    }

    @Test
    @DisplayName("노선 역 호출 정상 동작")
    void getStations() {
        //given
        addSection(line,station1,station2,10);

        //when
        List<Section> sections = line.getLineSection().getSections();

        //then
        assertThat(sections).hasSize(1);
        assertThat(sections.get(0).getUpStation().getName()).isEqualTo(염창역);
        assertThat(sections.get(0).getDownStation().getName()).isEqualTo(당산역);
        assertThat(sections.get(0).getDistance()).isEqualTo(10);
    }

    @Test
    @DisplayName("노선 구간 삭제 정상 동작")
    void removeSection() {
        //given
        addSection(line,station1,station2,10);
        addSection(line,station2,station3,10);

        //when
        line.getLineSection().remove(염창역);
        List<String> stationNames = getStationNames(line);

        //then
        assertThat(stationNames).doesNotContain(염창역);
        assertThat(stationNames).hasSize(1);
    }

    private List<String> getStationNames(Line line) {
        return line.getLineSection().getSections().stream()
            .map(v->v.getUpStation().getName())
            .collect(Collectors.toList());
    }

    private Line createLine() {
        return new Line(구호선,YELLOW);
    }

    private void addSection(Line line, Station station1, Station station2, int distance) {
        Section section1 = new Section(line, station1, station2, distance);
        line.getLineSection().add(section1);
    }

}
