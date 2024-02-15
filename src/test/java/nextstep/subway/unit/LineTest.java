package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import nextstep.subway.line.Line;
import nextstep.subway.section.Section;
import org.junit.jupiter.api.Test;

class LineTest {
    private static String name = "2호선";
    private static String green = "green";
    private static long upStationId = 1L;
    private static long downStationId = 2L;
    private static long distance = 10L;
    @Test
    void addSection() {
        //given
        Line line = 라인생성();
        Section section = 구간생성();

        //when
        line.addSection(section);

        //then
        assertThat(line.getAllStationId().size()).isEqualTo(2);
        assertThat(line.getUpStationId()).isEqualTo(upStationId);
        assertThat(line.getDownStationId()).isEqualTo(downStationId);
    }

    @Test
    void getStations() {
        //given
        Line line = 라인생성();
        Section section = 구간생성();
        line.addSection(section);

        //when
        List<Long> allStationId = line.getAllStationId();

        //then
        assertThat(allStationId.size()).isEqualTo(2);
        assertTrue(allStationId.contains(upStationId));
        assertTrue(allStationId.contains(downStationId));
    }

    @Test
    void removeSection() {
        //given
        Line line = 라인생성();
        Section section = 구간생성();
        line.addSection(section);
        long lastAddStationId = 3L;
        line.addSection(new Section(downStationId, lastAddStationId, 5L));

        //when
        line.removeStation(lastAddStationId);

        //then
        assertThat(line.getAllStationId().size()).isEqualTo(2);
    }

    private static Section 구간생성() {
        Section section = new Section(upStationId, downStationId, distance);
        return section;
    }

    private static Line 라인생성() {
        Line line = new Line(name, green);
        return line;
    }
}
