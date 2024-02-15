package subway.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import subway.line.Line;
import subway.section.Section;
import subway.station.Station;

import static org.assertj.core.api.Assertions.*;

public class LineTest {

    private final Line line = new Line("9호선", "bg-gold-600");
    private final Station dangsan = new Station("당산역");
    private final Station seonyudo = new Station("선유도역");
    private final Station Sinmokdong = new Station("신목동역");
    private final Section firstSection = new Section(dangsan, seonyudo, 10L, line);
    private final Section secondSection = new Section(seonyudo, Sinmokdong, 20L, line);

    @BeforeEach
    void beforeEach(){
        line.sections();
    }

    /**
     *  When 노선에 구간을 추가하면
     *  Then 추가된 구간들을 조회할 수 있다.
     */
    @Test
    void addSection() {
        // when
        line.addSection(firstSection);

        // then
        assertThat(line.sections().sections()).hasSize(1);
        assertThat(line.sections().sections()).contains(firstSection);
    }

    /**
     *  When 노선의 구간 조회 시
     *  Then 추가된 구간들을 조회할 수 있다.
     */
    @Test
    void getSections() {
        // when
        line.addSection(firstSection);
        line.addSection(secondSection);

        // then
        assertThat(line.sections().sections().get(0)).isEqualTo(firstSection);
        assertThat(line.sections().sections().get(1)).isEqualTo(secondSection);
    }

    /**
     *  When 노선의 구간을 추가후 삭제시
     *  Then 삭제한 구간을 조회할 수 없다.
     */
    @Test
    void removeSection() {
        line.sections().add(firstSection);
        line.remove(firstSection);

        assertThat(line.sections().sections()).hasSize(0);
    }
}
