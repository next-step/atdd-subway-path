package subway.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import subway.line.Line;
import subway.section.Path;
import subway.section.Section;
import subway.station.Station;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PathTest {
    private final Line line = new Line("9호선", "bg-gold-600");
    private final Station dangsan = new Station("당산역");
    private final Station seonyudo = new Station("선유도역");
    private final Station sinmokdong = new Station("신목동역");
    private final Section firstSection = new Section(dangsan, seonyudo, 10L, line);
    private final Section secondSection = new Section(seonyudo, sinmokdong, 20L, line);
    private final Section thirdSection = new Section(dangsan, sinmokdong, 5L, line);

    @BeforeEach
    void beforeEach(){
        line.sections();
    }

    @Test
    void getStations() {
        List<Section> sections = new ArrayList<>();
        sections.add(firstSection);
        sections.add(secondSection);
        sections.add(thirdSection);
        Path path = new Path(sections);
        List<Station> stations = path.getStations(dangsan, sinmokdong);

        // then
        assertThat(stations).hasSize(2);
        assertThat(stations.get(0).getName()).isEqualTo("당산역");
        assertThat(stations.get(1).getName()).isEqualTo("신목동역");
    }
}
