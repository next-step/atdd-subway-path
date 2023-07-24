package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LineServiceMockTest {

    @Mock
    private LineRepository lineRepository;

    @Mock
    private StationRepository stationRepository;


    private LineService lineService;

    @BeforeEach
    void setUp() {
        lineService = new LineService(lineRepository, stationRepository);
    }

    @DisplayName("노선에 section을 추가하기 성공한다")
    @Test
    void addSectionSuccess() {
        // given
        Line line = mock(Line.class);
        given(line.getId()).willReturn(1L);
        given(lineRepository.findById(any())).willReturn(Optional.of(line));
        given(stationRepository.findById(anyLong())).willReturn(Optional.of(new Station()));

        // when
        LineResponse lineResponse = lineService.addSection(1L, new SectionRequest(1L, 2L, 1));

        // then
        assertThat(lineService.searchById(lineResponse.getId())).isEqualTo(lineResponse);
    }

    @DisplayName("노선을 추가 요청시 대상인 노선 아이디가 존재하지 않으면 예외를 던진다")
    @Test
    void addSectionFailedByLineNotFound() {
        // given
        given(lineRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        SectionRequest sectionRequest = new SectionRequest(1L, 1L, 1);
        assertThatThrownBy(
                () -> lineService.addSection(1L, sectionRequest)
        ).isInstanceOf(LineNotFoundException.class);
    }

    @DisplayName("노선을 추가 요청시 추가할 스테이션 아이디가 존재하지 않으면 예외를 던진다")
    @Test
    void addSectionFailedByStationNotFound() {
        // given
        given(lineRepository.findById(anyLong())).willReturn(Optional.of(new Line()));
        given(stationRepository.findById(any())).willReturn(Optional.empty());

        // when
        SectionRequest sectionRequest = new SectionRequest(1L, 2L, 1);
        assertThatThrownBy(
                () -> lineService.addSection(1L, sectionRequest)
        ).isInstanceOf(StationNotFoundException.class);
    }

    @DisplayName("노선을 저장 성공한다")
    @Test
    void saveLineSuccess() {
        // given
        Station gangnamStation = new Station("강남역");
        Station yangjaeStation = new Station("양재역");
        Line line = new Line("신분당선", "#D31145", gangnamStation, yangjaeStation, 1);
        given(stationRepository.findById(1L)).willReturn(Optional.of(gangnamStation));
        given(stationRepository.findById(2L)).willReturn(Optional.of(yangjaeStation));
        given(lineRepository.save(any(Line.class))).willReturn(line);

        // when
        LineResponse lineResponse =
                lineService.saveLine(new LineRequest("신분당선", "#D31145", 1L, 2L, 3));

        // then
        assertThat(lineResponse).isEqualTo(LineResponse.from(line));
    }

    @DisplayName("노선을 저장 요청시 저장 info 중의 station이 존재하지 않으면 예외를 던진다")
    @Test
    void saveLineFailedByStationNotFound() {
        // given
        given(stationRepository.findById(any())).willReturn(Optional.empty());
        // when
        LineRequest lineRequest = new LineRequest("신분당선", "#123123", 1L, 2L, 3);
        assertThatThrownBy(
                () -> lineService.saveLine(lineRequest)
        ).isInstanceOf(StationNotFoundException.class);
    }

    @DisplayName("모든 노선을 보낸다")
    @Test
    void showLines() {
        // given
        Station gangnamStation = new Station("강남역");
        Station yangjaeStation = new Station("양재역");
        Station pangyoStation = new Station("판교역");
        Line shinundangLine = new Line("신분당선", "#D31145", gangnamStation, yangjaeStation, 1);
        shinundangLine.addSection(yangjaeStation, pangyoStation, 2);

        Line shinshinundangLine = new Line("신신분당선", "#D31146", pangyoStation, yangjaeStation, 2);
        shinshinundangLine.addSection(yangjaeStation, gangnamStation, 1);
        given(lineRepository.findAll()).willReturn(List.of(shinundangLine, shinshinundangLine));

        // when
        List<LineResponse> lineResponses = lineService.showLines();

        // then
        assertThat(lineResponses).contains(LineResponse.from(shinundangLine), LineResponse.from(shinshinundangLine));
    }

    @DisplayName("아이디로 노선 조회 성공")
    @Test
    void searchByIdSuccess() {
        // given
        Station gangnamStation = new Station("강남역");
        Station yangjaeStation = new Station("양재역");
        Line line = new Line("신분당선", "#D31145", gangnamStation, yangjaeStation, 1);
        given(lineRepository.findById(anyLong())).willReturn(Optional.of(line));

        // when
        LineResponse lineResponse = lineService.searchById(1L);

        // then
        assertThat(lineResponse).isEqualTo(LineResponse.from(line));
    }

    @DisplayName("아이디로 노선 조회 요청시 아이디가 존재하지 않으면 예외를 던진다")
    @Test
    void searchByIdFailedByLineNotFound() {
        // given
        given(lineRepository.findById(anyLong())).willReturn(Optional.empty());

        // when,then
        assertThatThrownBy(() -> lineService.searchById(1L)).isInstanceOf(LineNotFoundException.class);
    }

    @DisplayName("노선 정보를 업데이트 성공한다")
    @Test
    void updateSuccess() {
        // given
        Station gangnamStation = new Station("강남역");
        Station yangjaeStation = new Station("양재역");
        Line line = new Line("신분당선", "#D31145", gangnamStation, yangjaeStation, 1);
        given(lineRepository.findById(anyLong())).willReturn(Optional.of(line));

        // when
        lineService.update(1L, new UpdateLineRequest("분당선", "#23232"));

        // then
        Assertions.assertAll(
                () -> assertThat(line.getName()).isEqualTo("분당선"),
                () -> assertThat(line.getColor()).isEqualTo("#23232")
        );
    }

    @DisplayName("노선 정보를 업데이트 요청시 노선이 존재하지 않으면 예외를 던진다")
    @Test
    void updateFailedByLineNotFound() {
        // given
        given(lineRepository.findById(anyLong())).willReturn(Optional.empty());

        // when,then
        UpdateLineRequest updateLineRequest = new UpdateLineRequest("분당선", "#23232");
        assertThatThrownBy(() -> lineService.update(1L, updateLineRequest))
                .isInstanceOf(LineNotFoundException.class);
    }

    @DisplayName("아이디로 lineRepository를 통해 노선을 삭제한다")
    @Test
    void delete() {
        // when
        lineService.deleteById(1L);

        // then
        verify(lineRepository).deleteById(1L);
    }

    @DisplayName("마지막 하행스테이션으로 세션을 삭제 성공한다")
    @Test
    void deleteSectionSuccess() {
        // given
        Station pangyoStation = new Station("판교역");
        Station gangnamStation = new Station("강남역");
        Station yangjaeStation = new Station("양재역");
        Line line = new Line("신분당선", "#D31145", gangnamStation, yangjaeStation, 1);
        line.addSection(yangjaeStation, pangyoStation, 10);
        given(stationRepository.findById(anyLong())).willReturn(Optional.of(pangyoStation));
        given(lineRepository.findById(anyLong())).willReturn(Optional.of(line));

        // when
        lineService.deleteSection(1L, 1L);

        // then
        assertThat(line.getStations()).doesNotContain(pangyoStation);
    }

    @DisplayName("마지막 하행스테이션으로 세션을 삭제 요청시 대상 라인이 존재하지 않으면 예외를 던진다")
    @Test
    void deleteSectionFailedByLineNotExist() {
        // given
        given(stationRepository.findById(anyLong())).willReturn(Optional.of(new Station("강남역")));
        given(lineRepository.findById(anyLong())).willReturn(Optional.empty());

        // when,then
        assertThatThrownBy(() -> lineService.deleteSection(1L, 1L)).isInstanceOf(
                LineNotFoundException.class);
    }

    @DisplayName("마지막 하행스테이션으로 세션을 삭제 요청시 대상 스테이션이 존재하지 않으면 예외를 던진다")
    @Test
    void deleteSectionFailedByStationNotExist() {
        // given
        given(stationRepository.findById(anyLong())).willReturn(Optional.of(new Station("강남역")));
        given(lineRepository.findById(anyLong())).willReturn(Optional.empty());

        // when,then
        assertThatThrownBy(() -> lineService.deleteSection(1L, 1L)).isInstanceOf(
                LineNotFoundException.class);
    }

    @DisplayName("출발역으로 부터 도착역까지의 경로에 있는 역 목록 및 경로 구간의 거리 조회")
    @Test
    void findShortestPathBetweenStations() {
        // given
        Station gangnamStation = new Station("강남역");
        Station yangjaeStation = new Station("양재역");
        Line shinbundangLine = new Line("신분당선", "#D31145", gangnamStation, yangjaeStation, 1);

        Station dogokStation = new Station("도곡역");
        Station suseoStation = new Station("수서역");
        Line line3 = new Line("3호선", "#82C341", yangjaeStation, dogokStation, 2);
        line3.addSection(dogokStation, suseoStation, 4);

        Station seolleungStation = new Station("선릉역");
        Line line2 = new Line("2호선", "#0052A4", gangnamStation, seolleungStation, 2);

        Line bundangLine = new Line("분당선", "#82C341", seolleungStation, dogokStation, 2);
        bundangLine.addSection(dogokStation, suseoStation, 4);

        given(stationRepository.findById(1L)).willReturn(Optional.of(gangnamStation));
        given(stationRepository.findById(2L)).willReturn(Optional.of(suseoStation));
        given(lineRepository.findAll()).willReturn(List.of(shinbundangLine, line3, line2, bundangLine));

        // when
        PathResponse pathResponse = lineService.findShortestPathBetweenStations(1L, 2L);

        // then
        assertThat(pathResponse.getDistance()).isEqualTo(7);
        List<String> stationNameList = pathResponse.getStations().stream()
                .map(Station::getName)
                .collect(Collectors.toList());
        assertThat(stationNameList).isEqualTo(List.of("강남역", "양재역", "도곡역", "수서역"));
    }

    @DisplayName("조회시 출발역과 도착역이 같은 겨우 예외가 발생한다")
    @Test
    void findShortestPathBetweenStationsFailedBySameStation() {
        assertThatThrownBy(() -> lineService.findShortestPathBetweenStations(1L, 1L))
                .isInstanceOf(SameStationException.class);
    }

    @DisplayName("출발역과 도착역이 연결이 되어 있지 않은 경우 예외가 발생한다")
    @Test
    void findShortestPathBetweenStationsFailedByUnreachable() {
        // given
        Station gangnamStation = new Station("강남역");
        Station yangjaeStation = new Station("양재역");

        Station dogokStation = new Station("도곡역");
        Station suseoStation = new Station("수서역");
        Line line3 = new Line("3호선", "#82C341", dogokStation, suseoStation, 2);
        Line shinbundangLine = new Line("신분당선", "#D31145", gangnamStation, yangjaeStation, 1);

        Long gangnamStationId = gangnamStation.getId();
        Long suseoStationId = suseoStation.getId();
        given(stationRepository.findById(1L)).willReturn(Optional.of(gangnamStation));
        given(stationRepository.findById(2L)).willReturn(Optional.of(suseoStation));
        given(lineRepository.findAll()).willReturn(List.of(shinbundangLine, line3));

        // when,then
        assertThatThrownBy(() -> lineService.findShortestPathBetweenStations(1L, 2L))
                .isInstanceOf(UnreachableDestinationException.class);
    }


    @DisplayName("존재하지 않은 출발역이나 도착역을 조회 할 경우 경우 예외가 발생한다")
    @Test
    void findShortestPathBetweenStationsFailedByStationNotExists() {
        // given
        Station gangnamStation = new Station("강남역");
        given(stationRepository.findById(1L)).willReturn(Optional.of(gangnamStation));
        given(stationRepository.findById(2L)).willReturn(Optional.empty());

        // when,then
        Assertions.assertAll(
                () -> assertThatThrownBy(
                        () -> lineService.findShortestPathBetweenStations(1L, 2L))
                        .isInstanceOf(StationNotFoundException.class),
                () -> assertThatThrownBy(
                        () -> lineService.findShortestPathBetweenStations(2L, 1L))
                        .isInstanceOf(StationNotFoundException.class)
        );
    }
}


