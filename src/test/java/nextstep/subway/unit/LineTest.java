package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.util.List;
import java.util.Set;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.exception.LineException;
import nextstep.subway.unit.Fixtures.LineFixture;
import nextstep.subway.unit.Fixtures.StationFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {

    private Station 강남역;
    private Station 양재역;
    private Line 신분당선;

    @BeforeEach
    void setUp() {
        신분당선 = LineFixture.line(1L, "2호선", "green");
        강남역 = StationFixture.station(1L, "강남역");
        양재역 = StationFixture.station(2L, "양재역");
    }

    /***
     * When 지하철 노선에 지하철 구간을 추가하면
     * Then 지하철 노선에 지하철 구간이 추가된다
     */
    @Test
    void addSection() {
        // when
        신분당선.addSection(강남역, 양재역, 10);
        // then
        assertThat(신분당선.getSections()).isNotEmpty();
    }

    /***
     * Given 지하철 노선에 지하철 구간을 추가한다
     * When 지하철 노선의 지하철 역 목록을 조회하면
     * Then 지하철 노선의 지하철 역 목록을 조회할 수 있다
     *
     */
    @Test
    void getStations() {
        // given
        신분당선.addSection(강남역, 양재역, 10);

        // when
        Set<Station> stations = 신분당선.getStations();
        // then
        assertThat(stations).isNotEmpty();
        assertThat(stations.size()).isEqualTo(2);

    }

    /***
     * Given 지하철 노선에 지하철 구간을 추가한다
     * Given 지하철역을 하나 추가하고
     * Given 지하철 노선에 지하철 구간을 추가한다
     * When 지하철 노선의 상행종점역을 삭제하면
     * Then 지하철 노선의 지하철 구간이 제거된다
     */
    @Test
    void removeSection() {
        // given
        신분당선.addSection(강남역, 양재역, 10);
        Station 판교역 = StationFixture.station(3L, "판교역");
        // 강남역 양재역 양재역 판교역
        신분당선.addSection(양재역, 판교역, 10);

        // when
        신분당선.removeSection(강남역);
        // then
        assertThat(신분당선.getSections().size()).isEqualTo(1);
        assertThat(신분당선.getSections().get(0).getUpStation()).isEqualTo(양재역);
        assertThat(신분당선.getSections().get(0).getDownStation()).isEqualTo(판교역);
    }


    /**
     * Given 지하철 노선에 지하철 구간을 추가한다
     * When 지하철 노선에 구간이 하나밖에 없을 떄 구간을 제거하면 예외가 발생한다.
     * Then 예외가 발생한다.
     */
    @DisplayName("지하철 노선에 구간이 하나밖에 없을 떄 구간을 제거하면 예외가 발생한다.")
    @Test
    void removeSectionException() {
        // given
        신분당선.addSection(강남역, 양재역, 10);
        // when
        // then
        assertThatThrownBy(() -> 신분당선.removeSection(강남역))
            .isInstanceOf(LineException.class);
    }

    /***
     * Given 지하철 노선에 지하철 구간을 추가한다
     * When 지하철 노선 중간에 지하철 구간이 2개 추가되면
     * Then 지하철 노선에 지하철 구간이 2개 추가된다.
     */
    @Test
    void addSectionInMiddle() {
        // given
        신분당선.addSection(강남역, 양재역, 10);
        Station 정자역 = StationFixture.station(3L, "정자역");

        // when
        신분당선.addSection(강남역, 정자역, 4);

        Station 판교역 = StationFixture.station(4L, "판교역");
        신분당선.addSection(정자역, 판교역, 4);
        // then
        Section firstSection = 신분당선.getSections().get(0);
        Section secondSection = 신분당선.getSections().get(1);
        Section thirdSection = 신분당선.getSections().get(2);

        assertThat(firstSection.getDownStation()).isEqualTo(정자역);
        assertThat(secondSection.getDownStation()).isEqualTo(판교역);
        assertThat(thirdSection.getDownStation()).isEqualTo(양재역);
        assertThat(신분당선.getSections().size()).isEqualTo(3);

        assertThat(firstSection.getDistance()).isEqualTo(4);
        assertThat(secondSection.getDistance()).isEqualTo(4);
        assertThat(thirdSection.getDistance()).isEqualTo(2);
    }


    /***
     * Given 지하철 노선에 지하철 구간을 추가한다
     * When 지하철 노선 중간에 지하철 구간이 3개 추가되면
     * Then 지하철 노선에 지하철 구간이 3개 추가된 걸 getSections 로 알 수 있다.
     */
    @Test
    void getSections() {
        // given
        신분당선.addSection(강남역, 양재역, 10);
        // 중간 추가
        Station 정자역 = StationFixture.station(3L, "정자역");
        신분당선.addSection(강남역, 정자역, 4);

        // 맨 앞 추가
        Station 판교역 = StationFixture.station(4L, "판교역");
        신분당선.addSection(판교역, 강남역, 4);

        // 맨 뒤 추가
        Station 신사역 = StationFixture.station(5L, "신사역");
        신분당선.addSection(양재역, 신사역, 4);

        // when
        List<Section> sections = 신분당선.getSections();
        // then
        assertThat(sections.size()).isEqualTo(4);
        assertThat(sections.get(0).getUpStation()).isEqualTo(판교역);
        assertThat(sections.get(0).getDownStation()).isEqualTo(강남역);
        assertThat(sections.get(1).getUpStation()).isEqualTo(강남역);
        assertThat(sections.get(1).getDownStation()).isEqualTo(정자역);
        assertThat(sections.get(2).getUpStation()).isEqualTo(정자역);
        assertThat(sections.get(2).getDownStation()).isEqualTo(양재역);
        assertThat(sections.get(3).getUpStation()).isEqualTo(양재역);
        assertThat(sections.get(3).getDownStation()).isEqualTo(신사역);
    }

    /***
     * Given 지하철역 한 개를 생성하고
     * Given 지하철 노선에 지하철 구간을 추가한다
     * When 지하철 노선을 삭제하면
     * Then 지하철 노선이 삭제된다
     */
    @Test
    void removeSectionInMiddle() {
        // given
        신분당선.addSection(강남역, 양재역, 10);
        Station 정자역 = StationFixture.station(3L, "정자역");
        신분당선.addSection(강남역, 정자역, 4);
        assertThat(신분당선.getSections().get(0).getDistance()).isEqualTo(4);
        assertThat(신분당선.getSections().get(1).getDistance()).isEqualTo(6);
        Station 판교역 = StationFixture.station(4L, "판교역");
        신분당선.addSection(정자역, 판교역, 4);
        assertThat(신분당선.getSections().get(0).getDistance()).isEqualTo(4);
        assertThat(신분당선.getSections().get(1).getDistance()).isEqualTo(4);
        assertThat(신분당선.getSections().get(2).getDistance()).isEqualTo(2);
        // when
        신분당선.removeSection(정자역);
        // then
        List<Section> result = 신분당선.getSections();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getUpStation()).isEqualTo(정자역);
        assertThat(result.get(0).getDownStation()).isEqualTo(판교역);
        assertThat(result.get(1).getUpStation()).isEqualTo(판교역);
        assertThat(result.get(1).getDownStation()).isEqualTo(양재역);
    }


    /***
     * Given 지하철 노선에 지하철 구간을 추가한다
     * Given 지하철역 한 개를 생성하고
     * Given 지하철 노선에 지하철 구간을 추가한다
     * When 지하철 노선을 삭제하면
     * Then 지하철 노선이 삭제된다
     */
    @Test
    void removeSectionInMiddleByUpStation() {
        // given
        신분당선.addSection(강남역, 양재역, 10);
        Station 정자역 = StationFixture.station(3L, "정자역");
        신분당선.addSection(강남역, 정자역, 4);
        assertThat(신분당선.getSections().get(0).getDistance()).isEqualTo(4);
        assertThat(신분당선.getSections().get(1).getDistance()).isEqualTo(6);

        Station 판교역 = StationFixture.station(4L, "판교역");
        신분당선.addSection(정자역, 판교역, 4);
        assertThat(신분당선.getSections().get(0).getDistance()).isEqualTo(4);
        assertThat(신분당선.getSections().get(1).getDistance()).isEqualTo(4);
        assertThat(신분당선.getSections().get(2).getDistance()).isEqualTo(2);

        // when
        신분당선.removeSection(강남역);
        // then
        List<Section> result = 신분당선.getSections();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getUpStation()).isEqualTo(정자역);
        assertThat(result.get(0).getDownStation()).isEqualTo(판교역);
        assertThat(result.get(1).getUpStation()).isEqualTo(판교역);
        assertThat(result.get(1).getDownStation()).isEqualTo(양재역);
    }
}
