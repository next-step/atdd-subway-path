package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("지하철 노선 단위 테스트")
public class LineStationsTest {
    private LineStations lineStations;
    private Station station1;
    private Station station2;
    private Station station3;

    @BeforeEach
    void setUp() {
        // given
        lineStations = new LineStations();
        station1 = new Station("서울역");
        station2 = new Station("홍대입구");
        station3 = new Station("검암역");
        lineStations.registerLineStation(new LineStation(station1, null, 10, 10));
        lineStations.registerLineStation(new LineStation(station2, station1, 10, 10));
        lineStations.registerLineStation(new LineStation(station3, station2, 10, 10));
    }

    @DisplayName("지하철 노선에 역을 마지막에 등록한다.")
    @Test
    void add1() {
        // when
        Station newStation = new Station("인천공항");
        lineStations.registerLineStation(new LineStation(newStation, station3, 10, 10));

        // then
        List<Station> stations = lineStations.getLineStationsInOrder().stream()
                .map(LineStation::getStation)
                .collect(Collectors.toList());
        assertThat(stations).containsExactly(station1, station2, station3, newStation);
    }

    @DisplayName("지하철 노선에 역을 중간에 등록한다.")
    @Test
    void add2() {
        // when
        Station newStation = new Station("인천공함");
        LineStation newLineStation = new LineStation(newStation, station2, 10, 10);
        lineStations.registerLineStation(newLineStation);

        assertThat(lineStations.getLineStationsInOrder()).extracting(LineStation::getStation)
                .containsExactly(station1, station2, newStation, station3);
    }

    @DisplayName("이미 등록되어 있던 역을 등록한다.")
    @Test
    void add3() {
        // when
        assertThatThrownBy(() -> lineStations.registerLineStation(new LineStation(station2, station1, 10, 10)))
                .isInstanceOf(RuntimeException.class);
    }

    @DisplayName("지하철 노선에 등록된 마지막 지하철역을 제외한다.")
    @Test
    void removeLineStation1() {
    }

    @DisplayName("지하철 노선에 등록된 중간 지하철역을 제외한다.")
    @Test
    void removeLineStation2() {
    }

    @DisplayName("지하철 노선의 출발점을 제외한다.")
    @Test
    void removeLineStation3() {
    }

    @DisplayName("지하철 노선에서 등록되지 않는 역을 제외한다.")
    @Test
    void removeLineStation4() {
    }
}
