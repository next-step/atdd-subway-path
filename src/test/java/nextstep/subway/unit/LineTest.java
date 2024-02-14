package nextstep.subway.unit;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.util.List;
import java.util.Set;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
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

    /***
     * Given 지하철 노선을 생성하고
     * Given 지하철역 두 개를 생성하고
     * Given 지하철 구간을 생성한다
     * Given 지하철 노선에 지하철 구간을 추가한다
     * When 지하철 노선 중간에 지하철 구간이 2개 추가되면
     * Then 지하철 노선에 지하철 구간이 2개 추가된다.
     */
    @Test
    void addSectionInMiddle() {
        // given
        Line line = LineFixture.line(1L, "2호선", "green");
        Station upStation = StationFixture.station(1L, "강남역");
        Station downStation = StationFixture.station(2L, "양재역");
        line.addSection(upStation, downStation, 10);
        Station middleStation = StationFixture.station(3L, "정자역");

        // when
        line.addSection(upStation, middleStation, 4);

        Station downStation2 = StationFixture.station(4L, "판교역");
        line.addSection(middleStation, downStation2, 4);
        // then
        Section firstSection = line.getSections().get(0);
        Section secondSection = line.getSections().get(1);
        Section thirdSection = line.getSections().get(2);

        assertThat(firstSection.getDownStation()).isEqualTo(middleStation);
        assertThat(secondSection.getDownStation()).isEqualTo(downStation2);
        assertThat(thirdSection.getDownStation()).isEqualTo(downStation);
        assertThat(line.getSections().size()).isEqualTo(3);

        assertThat(firstSection.getDistance()).isEqualTo(4);
        assertThat(secondSection.getDistance()).isEqualTo(4);
        assertThat(thirdSection.getDistance()).isEqualTo(2);
    }


    /***
     * Given 지하철 노선을 생성하고
     * Given 지하철역 두 개를 생성하고
     * Given 지하철 구간을 생성한다
     * Given 지하철 노선에 지하철 구간을 추가한다
     * When 지하철 노선 중간에 지하철 구간이 2개 추가되면
     * Then 지하철 노선에 지하철 구간이 2개 추가된다.
     */
    @Test
    void getSections() {
        // given
        Line line = LineFixture.line(1L, "2호선", "green");
        Station upStation = StationFixture.station(1L, "강남역");
        Station downStation = StationFixture.station(2L, "양재역");
        line.addSection(upStation, downStation, 10);
        // 중간 추가
        Station middleStation = StationFixture.station(3L, "정자역");
        line.addSection(upStation, middleStation, 4);

        // 맨 앞 추가
        Station newUpStation = StationFixture.station(4L, "판교역");
        line.addSection(newUpStation, upStation, 4);

        // 맨 뒤 추가
        Station newDownStation = StationFixture.station(5L, "신사역");
        line.addSection(downStation, newDownStation, 4);

        // when
        List<Section> sections = line.getSections();
        // then
        assertThat(sections.size()).isEqualTo(4);
        assertThat(sections.get(0).getUpStation()).isEqualTo(newUpStation);
        assertThat(sections.get(0).getDownStation()).isEqualTo(upStation);
        assertThat(sections.get(1).getUpStation()).isEqualTo(upStation);
        assertThat(sections.get(1).getDownStation()).isEqualTo(middleStation);
        assertThat(sections.get(2).getUpStation()).isEqualTo(middleStation);
        assertThat(sections.get(2).getDownStation()).isEqualTo(downStation);
    }

}
