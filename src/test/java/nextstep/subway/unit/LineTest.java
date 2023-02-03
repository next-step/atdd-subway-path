package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {
    @Test
    @DisplayName("구간 추가")
    void addSection() {
        //given 노선을 생성한다.
        Line line = new Line("4호선", "sky-blue");
        Station 당고개 = new Station("당고개");
        Station 상계 = new Station("상계");

        //when 노선에 구간을 추가한다.
        new Section(line, 당고개, 상계, 10);

        //then 노선에서 구간이 조회된다.
        assertThat(line.getAllStations()
                .stream().map(station -> station.getName())).containsOnly("당고개", "상계");

    }

    @Test
    void getStations() {
    }

    @Test
    void removeSection() {
    }
}
