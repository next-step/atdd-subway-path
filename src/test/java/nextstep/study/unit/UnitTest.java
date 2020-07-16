package nextstep.study.unit;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineStation;

@DisplayName("단위 테스트")
public class UnitTest {
    @DisplayName("단위 테스트 - 단독")
    @Test
    void update() {
        // given
        Line line = new Line("신분당선", "RED", LocalTime.now(), LocalTime.now(), 10);
        String newName = "새이름";
        Line newLine = new Line(newName, "RED", LocalTime.now(), LocalTime.now(), 10);

        // when
        line.update(newLine);

        // then
        assertThat(line.getName()).isEqualTo(newName);
    }

    @DisplayName("단위 테스트 - 협력 객체 사용")
    @Test
    void addLineStation() {
        // given
        Line line = new Line("신분당선", "RED", LocalTime.now(), LocalTime.now(), 10);
        LineStation lineStation = new LineStation(1L, null, 10, 10);

        // when
        line.addLineStation(lineStation);

        // then
        assertThat(line.getStationInOrder()).hasSize(1);
    }
}
