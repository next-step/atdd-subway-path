package nextstep.subway.unit.line;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.Station;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class LineTest {

    private Line 이호선;
    private Station 잠실역;
    private Station 성수역;

    @BeforeEach
    void setUp() {
        이호선 = new Line("이호선", "green");
        잠실역 = new Station(1L, "잠실역");
        성수역 = new Station(2L, "성수역");
    }

    @DisplayName("노선에 구간을 등록한다.")
    @Test
    void addSection() {
        //when
        이호선.generateSection(10, 잠실역, 성수역);

        //then
        assertThat(이호선.getSectionList()).hasSize(1);
    }

    @DisplayName("노선에 등록된 역들을 조회한다.")
    @Test
    void getStations() {
        //given
        이호선.generateSection(10, 잠실역, 성수역);

        //when
        List<Station> stations = 이호선.getSections().getStations();

        //then
        assertThat(stations).hasSize(2);
        assertThat(stations).containsExactly(잠실역, 성수역);
    }

    @DisplayName("노선에 등록된 구간을 삭제한다.")
    @Test
    void removeSection() {
        //given
        이호선.generateSection(10, 잠실역, 성수역);

        Station 건대입구역 = new Station(3L, "건대입구역");
        이호선.generateSection(5, 성수역, 건대입구역);

        //when
        이호선.deleteSection(건대입구역);

        //then
        assertThat(이호선.getSectionList()).hasSize(1);
    }
}
