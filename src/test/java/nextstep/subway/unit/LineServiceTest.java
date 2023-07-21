package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import nextstep.subway.line.Line;
import nextstep.subway.line.LineNotFoundException;
import nextstep.subway.line.LineRepository;
import nextstep.subway.line.LineRequest;
import nextstep.subway.line.LineResponse;
import nextstep.subway.line.LineService;
import nextstep.subway.line.SectionRequest;
import nextstep.subway.line.UpdateLineRequest;
import nextstep.subway.station.Station;
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
public class LineServiceTest {

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
        assertThatThrownBy(
                () -> lineRepository.findById(shinundangLine.getId()).orElseThrow(LineNotFoundException::new))
                .isInstanceOf(LineNotFoundException.class);
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

}
