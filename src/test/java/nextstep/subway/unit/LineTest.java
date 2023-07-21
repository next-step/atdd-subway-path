package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import nextstep.subway.line.Line;
import nextstep.subway.station.Station;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {

    Station gangnamStation;
    Station yangjaeStation;

    @BeforeEach
    void setUp() {
        gangnamStation = new Station("강남역");
        yangjaeStation = new Station("양재역");
    }

    @DisplayName("세션을 성공적으로 추가한다")
    @Test
    void addSectionSuccess() {
        // given
        Line line = new Line("신분당선", "#D31145", gangnamStation, yangjaeStation, 1);
        Station pangyoStation = new Station("판교역");

        // when
        line.addSection(yangjaeStation, pangyoStation, 2);

        // then
        assertThat(line.getStations()).containsExactly(gangnamStation, yangjaeStation, pangyoStation);
    }

    @DisplayName("추가할 세션의 하행 스테이션이 노선의 상행 종점일 때 추가할 세션이 상행 좀점 세션으로 등록된다")
    @Test
    void insertSectionSuccessToTop() {
        // given
        Station pangyoStation = new Station("판교역");
        Line line = new Line("신분당선", "#D31145", yangjaeStation, pangyoStation, 1);

        // when
        line.addSection(gangnamStation, yangjaeStation, 1);

        // then
        Assertions.assertAll(
                () -> assertThat(line.getStations()).containsExactly(gangnamStation, yangjaeStation, pangyoStation)
        );
    }

    @DisplayName("추가할 세션의 상행 스테이션이 추가될 노선에 포함되고 추가될 위치의 하행스테이션과 똑같이 아니면 그사이에 추가된다")
    @Test
    void insertSectionSuccessBySameUpStationOfSection() {
        // given
        Station pangyoStation = new Station("판교역");
        Line line = new Line("신분당선", "#D31145", gangnamStation, pangyoStation, 10);

        // when
        line.addSection(gangnamStation, yangjaeStation, 1);

        // then
        assertThat(line.getStations()).containsExactly(gangnamStation, yangjaeStation, pangyoStation);
    }

    @DisplayName("노선에 포함한 스테이션을 가져온다")
    @Test
    void getStations() {
        // given
        Line line = new Line("신분당선", "#D31145", gangnamStation, yangjaeStation, 10);

        // when
        List<Station> stations = line.getStations();

        // then
        assertThat(stations).containsExactly(gangnamStation, yangjaeStation);
    }

    @DisplayName("노선에서 등록된 station을 성공적으로 삭제한다")
    @Test
    void deleteSectionSuccess() {
        // given
        Station pangyoStation = new Station("판교역");
        Line line = new Line("신분당선", "#D31145", gangnamStation, yangjaeStation, 10);
        line.addSection(yangjaeStation, pangyoStation, 10);

        // when
        line.deleteSection(pangyoStation);

        // then
        assertThat(line.getStations()).doesNotContain(pangyoStation);
    }

    @DisplayName("노선의 이름과 컬러를 업데이트 한다")
    @Test
    void update() {
        // given
        Line line = new Line("신분당선", "#D31145", gangnamStation, yangjaeStation, 10);

        // when
        line.update("강남선", "#D31146");

        // then
        Assertions.assertAll(
                () -> assertThat(line.getName()).isEqualTo("강남선"),
                () -> assertThat(line.getColor()).isEqualTo("#D31146")
        );
    }

}
