package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import nextstep.subway.line.Line;
import nextstep.subway.line.LineNotFoundException;
import nextstep.subway.line.LineRepository;
import nextstep.subway.line.LineRequest;
import nextstep.subway.line.LineResponse;
import nextstep.subway.line.LineService;
import nextstep.subway.line.PathResponse;
import nextstep.subway.line.SameStationException;
import nextstep.subway.line.SectionRequest;
import nextstep.subway.line.UnreachableDestinationException;
import nextstep.subway.line.UpdateLineRequest;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationNotFoundException;
import nextstep.subway.station.StationRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class LineServiceTest {

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private LineService lineService;
    Station gangnamStation;
    Station yangjaeStation;

    @BeforeEach
    void setUp() {
        gangnamStation = stationRepository.save(new Station("강남역"));
        yangjaeStation = stationRepository.save(new Station("양재역"));
    }

    @DisplayName("노선에 section을 추가한다")
    @Test
    void addSection() {
        // given
        Station pangyoStation = stationRepository.save(new Station("판교역"));
        Line line = new Line("신분당선", "#D31145", yangjaeStation, pangyoStation, 1);
        Line shinundangLine = lineRepository.save(line);
        SectionRequest sectionRequest = new SectionRequest(gangnamStation.getId(), yangjaeStation.getId(), 1);

        // when
        lineService.addSection(shinundangLine.getId(), sectionRequest);

        // then
        assertThat(line.getStations()).containsExactly(gangnamStation, yangjaeStation, pangyoStation);
    }

    @DisplayName("노선을 저장한다")
    @Test
    void saveLine() {
        // given
        LineRequest lineRequest = new LineRequest("신분당선", "#D31145", gangnamStation.getId(), yangjaeStation.getId(), 1);

        // when
        LineResponse lineResponse = lineService.saveLine(lineRequest);

        // then
        Line savedLine = lineRepository.getReferenceById(lineResponse.getId());
        assertThat(lineResponse).isEqualTo(LineResponse.from(savedLine));
    }

    @DisplayName("모든 노선을 보낸다")
    @Test
    void showLines() {
        // given
        Station pangyoStation = stationRepository.save(new Station("판교역"));

        Line line = new Line("신분당선", "#D31145", gangnamStation, yangjaeStation, 1);
        line.addSection(yangjaeStation, pangyoStation, 2);
        Line shinundangLine = lineRepository.save(line);

        line = new Line("신신분당선", "#D31146", pangyoStation, yangjaeStation, 2);
        line.addSection(yangjaeStation, gangnamStation, 1);
        Line shinshinundangLine = lineRepository.save(line);

        // when
        List<LineResponse> lineResponses = lineService.showLines();

        // then
        Assertions.assertAll(
                () -> assertThat(lineResponses).containsOnly(
                        LineResponse.from(shinshinundangLine), LineResponse.from(shinundangLine))
        );
    }

    @DisplayName("아이디로 노선 조회")
    @Test
    void searchById() {
        // given
        Station pangyoStation = stationRepository.save(new Station("판교역"));

        Line line = new Line("신분당선", "#D31145", gangnamStation, yangjaeStation, 1);
        line.addSection(yangjaeStation, pangyoStation, 2);
        Line shinundangLine = lineRepository.save(line);

        // when
        LineResponse lineResponse = lineService.searchById(shinundangLine.getId());

        // then
        assertThat(lineResponse).isEqualTo(LineResponse.from(line));
    }

    @DisplayName("line 정보를 업데이트 한다")
    @Test
    void update() {
        // given
        Station pangyoStation = stationRepository.save(new Station("판교역"));
        Line line = new Line("신분당선", "#D31145", gangnamStation, yangjaeStation, 1);
        line.addSection(yangjaeStation, pangyoStation, 2);
        Line shinundangLine = lineRepository.save(line);
        UpdateLineRequest updateLineRequest = new UpdateLineRequest("분당선", "#D31146");

        // when
        lineService.update(shinundangLine.getId(), updateLineRequest);

        // then
        Line savedLine = lineRepository.findById(shinundangLine.getId()).orElseThrow();
        Assertions.assertAll(
                () -> assertThat(savedLine.getName()).isEqualTo("분당선"),
                () -> assertThat(savedLine.getColor()).isEqualTo("#D31146")
        );
    }

    @DisplayName("id로 line를 삭제한다")
    @Test
    void deleteById() {
        // given
        Station pangyoStation = stationRepository.save(new Station("판교역"));
        Line line = new Line("신분당선", "#D31145", gangnamStation, yangjaeStation, 1);
        line.addSection(yangjaeStation, pangyoStation, 2);
        Line shinundangLine = lineRepository.save(line);

        // when
        lineService.deleteById(shinundangLine.getId());

        // then
        Optional<Line> foundLine = lineRepository.findById(shinundangLine.getId());
        assertThat(foundLine).isEmpty();
    }

    @DisplayName("최 하행 스테이션으로 마지막 세션을 삭제한다")
    @Test
    void deleteSection() {
        // given
        Station pangyoStation = stationRepository.save(new Station("판교역"));
        Line line = new Line("신분당선", "#D31145", gangnamStation, yangjaeStation, 1);
        line.addSection(yangjaeStation, pangyoStation, 2);
        Line shinundangLine = lineRepository.save(line);

        // when
        lineService.deleteSection(shinundangLine.getId(), pangyoStation.getId());

        // then
        Line savedLine = lineRepository.findById(shinundangLine.getId()).orElseThrow(LineNotFoundException::new);
        Assertions.assertAll(
                () -> assertThat(
                        savedLine.getStations()).contains(gangnamStation, yangjaeStation)
        );
    }


    /**
     * 강남역    --- *2호선* --- 선릉역
     * |                        |
     * *신분당선*               *분당선*
     * |                        |
     * 양재역    --- *3호선* --- 도곡역  --- *3호선* ---수서역
     *                          |                  |
     *                              --- *분당선* ---
     */
    @DisplayName("출발역으로 부터 도착역까지의 경로에 있는 역 목록 및 경로 구간의 거리 조회")
    @Test
    void findShortestPathBetweenStations() {
        // given
        Line shinbundangLine = new Line("신분당선", "#D31145", gangnamStation, yangjaeStation, 1);
        lineRepository.save(shinbundangLine);

        Station dogokStation = stationRepository.save(new Station("도곡역"));
        Station suseoStation = stationRepository.save(new Station("수서역"));
        Line line3 = new Line("3호선", "#82C341", yangjaeStation, dogokStation, 2);
        line3.addSection(dogokStation, suseoStation, 4);
        lineRepository.save(line3);

        Station seolleungStation = stationRepository.save(new Station("선릉역"));
        Line line2 = new Line("2호선", "#0052A4", gangnamStation, seolleungStation, 2);
        lineRepository.save(line2);

        Line bundangLine = new Line("분당선", "#82C341", seolleungStation, dogokStation, 2);
        bundangLine.addSection(dogokStation, suseoStation, 4);

        // when
        PathResponse pathResponse = lineService.findShortestPathBetweenStations(gangnamStation.getId(),
                suseoStation.getId());

        // when
        List<String> stationNames = pathResponse.getStations().stream()
                .map(Station::getName)
                .collect(Collectors.toList());
        Assertions.assertAll(
                () -> assertThat(stationNames).isEqualTo(List.of("강남역", "양재역", "도곡역", "수서역")),
                () -> assertThat(pathResponse.getDistance()).isEqualTo(7)
        );
    }

    @DisplayName("경로 조회시 출발역과 도착역이 같은 겨우 예외가 발생한다")
    @Test
    void findShortestPathBetweenStationsFailedBySameStation() {
        assertThatThrownBy(() -> lineService.findShortestPathBetweenStations(1L, 1L))
                .isInstanceOf(SameStationException.class);
    }

    @DisplayName("출발역과 도착역이 연결이 되어 있지 않은 경로 조회 시 예외가 발생한다")
    @Test
    void findShortestPathBetweenStationsFailedByUnreachable() {
        // given
        Line shinbundangLine = new Line("신분당선", "#D31145", gangnamStation, yangjaeStation, 1);
        lineRepository.save(shinbundangLine);

        Station dogokStation = stationRepository.save(new Station("도곡역"));
        Station suseoStation = stationRepository.save(new Station("수서역"));
        Line line3 = new Line("3호선", "#82C341", dogokStation, suseoStation, 2);
        lineRepository.save(line3);

        Long gangnamStationId = gangnamStation.getId();
        Long suseoStationId = suseoStation.getId();

        // when,then
        assertThatThrownBy(() -> lineService.findShortestPathBetweenStations(gangnamStationId, suseoStationId))
                .isInstanceOf(UnreachableDestinationException.class);
    }


    @DisplayName("존재하지 않은 출발역이나 도착역을 포함한 경로 조회 시 예외가 발생한다")
    @Test
    void findShortestPathBetweenStationsFailedByStationNotExists() {
        // given
        long noExistsId = 1000L;
        Long gangnamStationId = gangnamStation.getId();

        // when,then
        Assertions.assertAll(
                () -> assertThatThrownBy(
                        () -> lineService.findShortestPathBetweenStations(gangnamStationId, noExistsId))
                        .isInstanceOf(StationNotFoundException.class),
                () -> assertThatThrownBy(
                        () -> lineService.findShortestPathBetweenStations(noExistsId, gangnamStationId))
                        .isInstanceOf(StationNotFoundException.class)
        );
    }
}
