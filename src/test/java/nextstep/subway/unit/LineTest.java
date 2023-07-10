package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Line 단위 테스트")
class LineTest {

    /**
     * Given 노선이 있고
     * When 구간을 만들면
     * Then 노선에 구간이 추가 된다.
     */
    @DisplayName("노선에 구간을 추가 한다")
    @Test
    void addSection() {
        // given
        Line line = new Line("2호선", "bg-green-600");
        Station upStation = new Station("강남역");
        Station downStation = new Station("역삼역");
        final int distance = 10;

        // when
        Section section = new Section(line, upStation, downStation, distance);
        line.appendSection(section);

        // then
        assertThat(line.getSections().size()).isEqualTo(1);

    }

    /**
     *  Given 구간이 있는 노선이 있고
     *  When 역을 조회하면
     *  Then 역 목록을 얻을 수 있다
     */
    @DisplayName("노선에서 역을 조회한다.")
    @Test
    void getStations() {
        // given
        Line line = 이호선_기본구간_생성();

        // when
        List<Station> stations = line.getStations();

        // then
        assertThat(stations.size()).isEqualTo(2);
    }



    /**
     * Given 구간이 2개 이상인 노선이 있고
     * When 마지막 구간을 삭제하려고 하면
     * Then 구간을 삭제할 수 있다
     */
    @DisplayName("노선에서 구간을 삭제한다.")
    @Test
    void removeSection() {
        // given
        Line line = 이호선_기본구간_생성();
        Section section = 기본구간에_구간추가(line);

        // when
        line.removeSection(section);

        // then
        List<Section> sections = line.getSections();
        assertThat(sections.size()).isEqualTo(1);
    }

    private Line 이호선_기본구간_생성() {
        Line line = new Line("2호선", "bg-green-600");
        Station upStation = new Station("강남역");
        Station downStation = new Station("역삼역");
        final int distance = 10;
        Section section = new Section(line, upStation, downStation, distance);
        line.appendSection(section);
        return line;
    }

    private Section 기본구간에_구간추가(Line line) {
        Station upStation = new Station("역삼역");
        Station downStation = new Station("잠실역");
        final int distance = 10;
        Section section = new Section(line, upStation, downStation, distance);
        line.appendSection(section);
        return section;
    }
}
