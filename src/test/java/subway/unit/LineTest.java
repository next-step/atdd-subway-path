package subway.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import subway.line.Line;
import subway.section.Section;
import subway.station.Station;

import static org.assertj.core.api.Assertions.*;

public class LineTest {

    private final Line line = new Line(1L, "9호선", "bg-gold-600");
    private final Station dangsan = new Station(1L, "당산역");
    private final Station seonyudo = new Station(2L, "선유도역");
    private final Station sinmokdong = new Station(3L, "신목동역");
    private final Station yeouido = new Station(4L, "여의도역");
    private final Station nationalAssembly = new Station(5L, "국회의사당역");
    private final Section firstSection = new Section(1L, yeouido, dangsan, 30L, line);
    private final Section secondSection = new Section(2L, dangsan, sinmokdong, 40L, line);
    private final Section thirdSection = new Section(3L, yeouido, nationalAssembly, 10L, line);
    private final Section fourthSection = new Section(4L, seonyudo, sinmokdong, 20L, line);
    private final Section resultSection1 = new Section(5L, nationalAssembly, dangsan, 20L, line);
    private final Section resultSection2 = new Section(6L, dangsan, seonyudo, 20L, line);

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

    /**
     *  When 노선 맨 앞에 구간을 추가하면
     *  Then 추가된 구간들을 조회할 수 있다.
     */
    @Test
    void 노선_맨_앞에_구간을_추가할_수_있다() {
        // when
        line.addSection(secondSection);
        line.addSection(firstSection);

        // then
        assertThat(line.sections().sections()).hasSize(2);
        assertThat(line.sections().sections().get(0)).isEqualTo(secondSection);
        assertThat(line.sections().sections().get(1)).isEqualTo(firstSection);
    }

    /**
     *  When 신규 노선의 상행역이 기존 구간의 상행역과 같다면 기존 구간 중간에 추가되며
     *  Then 추가된 구간들을 조회할 수 있다.
     */
    @Test
    void 신규_노선의_상행역이_기존_구간의_상행역과_같다면_기존_구간_중간에_추가할_수_있다() {
        // when
        line.addSection(firstSection);
        line.addSection(thirdSection);

        // then
        assertThat(line.sections().sections()).hasSize(2);
        assertThat(line.sections().sections().get(0).getUpStation().getName())
                .isEqualTo(resultSection1.getUpStation().getName());
        assertThat(line.sections().sections().get(0).getDownStation().getName())
                .isEqualTo(resultSection1.getDownStation().getName());
        assertThat(line.sections().sections().get(1).getUpStation().getName())
                .isEqualTo(thirdSection.getUpStation().getName());
        assertThat(line.sections().sections().get(1).getDownStation().getName())
                .isEqualTo(thirdSection.getDownStation().getName());
    }

    /**
     *  When 노선 맨 앞에 구간을 추가하면
     *  Then 추가된 구간들을 조회할 수 있다.
     */
    @Test
    void 신규_노선의_하행역이_기존_구간의_하행역과_같다면_기존_구간_중간에_추가할_수_있다() {
        // when
        line.addSection(secondSection);
        line.addSection(fourthSection);

        // then
        assertThat(line.sections().sections()).hasSize(2);
        assertThat(line.sections().sections().get(0).getUpStation().getName())
                .isEqualTo(fourthSection.getUpStation().getName());
        assertThat(line.sections().sections().get(0).getDownStation().getName())
                .isEqualTo(fourthSection.getDownStation().getName());
        assertThat(line.sections().sections().get(1).getUpStation().getName())
                .isEqualTo(resultSection2.getUpStation().getName());
        assertThat(line.sections().sections().get(1).getDownStation().getName())
                .isEqualTo(resultSection2.getDownStation().getName());
    }
}
