package nextstep.subway.unit;

import nextstep.subway.exception.BadRequestException;
import nextstep.subway.line.Line;
import nextstep.subway.linesection.LineSection;
import nextstep.subway.station.Station;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 단위테스트")
public class LineTest {
    private Station 노원역;
    private Station 창동역;
    private Station 총신대입구역;
    private Station 사당역;
    private Line line;

    @BeforeEach
    void setUp() {
        노원역 = getStation(1L, "노원역");
        창동역 = getStation(2L, "창동역");
        총신대입구역 = getStation(3L, "총신대입구역");
        사당역 = getStation(4L, "사당역");
        line = Line.of("4호선", "light-blue", 노원역, 창동역, 3);
    }

    @DisplayName("구간 추가 - 새로운 역을 하행 종점으로 등록할 경우")
    @Test
    void addSection() {
        //given
        //when
        line.addSection(LineSection.of(line, 창동역, 사당역, 10));
        //then
        List<Station> stations = line.getStations();
        assertThat(stations.size()).isEqualTo(3);
        assertThat(stations).containsExactly(노원역, 창동역, 사당역);
    }

    /**
     * 노원역 -> 창동역 -> 사당역
     * 창동역 -> 총신대입구역 추가
     */
    @DisplayName("구간 추가 - 중간에 추가(구간 상행역과 추가할 상행역이 같은 경우)")
    @Test
    void addSectionInMiddle_case0() {
        //given
        line.addSection(LineSection.of(line, 창동역, 사당역, 10));
        //when
        line.addSection(LineSection.of(line, 창동역, 총신대입구역, 3));
        //then
        List<Station> stations = line.getStations();
        assertAll(
                () -> assertThat(stations.size()).isEqualTo(4),
                () -> assertThat(stations).containsExactly(노원역, 창동역, 총신대입구역, 사당역),
                () -> assertThat(line.getSections().getLastSection().getDistance()).isEqualTo(7));
    }

    /**
     * 노원역 -> 창동역 -> 사당역
     * 총신대입구역 -> 사당역 추가
     */
    @DisplayName("구간 추가 - 중간에 추가(구간 하행역과 추가할 하행역이 같은 경우)")
    @Test
    void addSectionInMiddle_case1() {
        //given
        line.addSection(LineSection.of(line, 창동역, 사당역, 10));
        //when
        line.addSection(LineSection.of(line, 총신대입구역, 사당역, 3));
        //then
        List<Station> stations = line.getStations();
        assertAll(
                () -> assertThat(stations.size()).isEqualTo(4),
                () -> assertThat(stations).containsExactly(노원역, 창동역, 총신대입구역, 사당역),
                () -> assertThat(line.getSections().getLastSection().getDistance()).isEqualTo(3));
    }

    /**
     * 노원역 -> 창동역 -> 총신대입구역
     * 사당역 -> 노원역 추가
     */
    @DisplayName("구간 추가 - 새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void addSectionInMiddle_case2() {
        //given
        line.addSection(LineSection.of(line, 창동역, 총신대입구역, 10));
        //when
        line.addSection(LineSection.of(line, 사당역, 노원역, 3));
        //then
        List<Station> stations = line.getStations();
        assertAll(
                () -> assertThat(stations.size()).isEqualTo(4),
                () -> assertThat(stations).containsExactly(사당역, 노원역, 창동역, 총신대입구역),
                () -> assertThat(line.getSections().getFirstSection().getDistance()).isEqualTo(3));
    }

    @DisplayName("구간 조회")
    @Test
    void getStations() {
        //given
        line.addSection(LineSection.of(line, 창동역, 사당역, 3));
        //when
        //then
        assertThat(line.getStations()).containsExactly(노원역, 창동역, 사당역);
    }

    @DisplayName("구간 제거")
    @Test
    void removeSection() {
        //given
        line.addSection(LineSection.of(line, 창동역, 사당역, 3));
        //when
        line.removeSection(사당역);
        //then
        assertThat(line.getStations()).containsExactly(노원역, 창동역);
    }


    private Station getStation(Long id, String name) {
        Station station = new Station(name);
        ReflectionTestUtils.setField(station, "id", id);
        return station;
    }
}
