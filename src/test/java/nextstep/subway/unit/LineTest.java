package nextstep.subway.unit;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.util.Set;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.unit.Fixtures.LineFixture;
import nextstep.subway.unit.Fixtures.StationFixture;
import org.junit.jupiter.api.Test;

class LineTest {

    /***
     * Given 지하철 노선을 생성하고
     * Given 지하철역 두 개를 생성하고
     * Given 지하철 구간을 생성한다
     * When 지하철 노선에 지하철 구간을 추가하면
     * Then 지하철 노선에 지하철 구간이 추가된다
     */
    @Test
    void addSection() {
        // given
        Line line = LineFixture.line(1L, "2호선", "green");
        Station upStation = StationFixture.station(1L, "강남역");
        Station downStation = StationFixture.station(2L, "양재역");
        // when
        line.addSection(upStation, downStation, 10);
        // then
        assertThat(line.getSections()).isNotEmpty();
    }

    /***
     * Given 지하철 노선을 생성하고
     * Given 지하철역 두 개를 생성하고
     * Given 지하철 구간을 생성한다
     * Given 지하철 노선에 지하철 구간을 추가한다
     * When 지하철 노선의 지하철 역 목록을 조회하면
     * Then 지하철 노선의 지하철 역 목록을 조회할 수 있다
     *
     */
    @Test
    void getStations() {
        // given
        Line line = LineFixture.line(1L, "2호선", "green");
        Station upStation = StationFixture.station(1L, "강남역");
        Station downStation = StationFixture.station(2L, "양재역");
        line.addSection(upStation, downStation, 10);

        // when
        Set<Station> stations = line.getStations();
        // then
        assertThat(stations).isNotEmpty();
        assertThat(stations.size()).isEqualTo(2);

    }

    /***
     * Given 지하철 노선을 생성하고
     * Given 지하철역 두 개를 생성하고
     * Given 지하철 구간을 생성한다
     * Given 지하철 노선에 지하철 구간을 추가한다
     * When 지하철 노선의 지하철 구간을 제거하면
     * Then 지하철 노선의 지하철 구간이 제거된다
     */
    @Test
    void removeSection() {
        // given
        Line line = LineFixture.line(1L, "2호선", "green");
        Station upStation = StationFixture.station(1L, "강남역");
        Station downStation = StationFixture.station(2L, "양재역");
        line.addSection(upStation, downStation, 10);

        // when
        line.removeSection(line.getSections().size() - 1);
        // then
        assertThat(line.getSections()).isEmpty();
    }
}
