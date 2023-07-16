package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nextstep.subway.line.Line;
import nextstep.subway.line.LineNotFoundException;
import nextstep.subway.line.LineRepository;
import nextstep.subway.line.LineRequest;
import nextstep.subway.line.LineResponse;
import nextstep.subway.line.LineService;
import nextstep.subway.line.Section;
import nextstep.subway.line.SectionRequest;
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
public class LineServiceMockTest {

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
        Line line = new Line("신분당선", "#123123", new ArrayList<>());
        given(lineRepository.findById(any())).willReturn(Optional.of(line));
        given(stationRepository.findById(anyLong())).willReturn(Optional.of(new Station()));

        // when
        LineResponse lineResponse = lineService.addSection(1L, new SectionRequest(1L, 1L, 1));

        // then
        assertThat(lineService.searchById(lineResponse.getId())).isEqualTo(lineResponse);
    }

    @DisplayName("노선을 추가 요청시 대상인 노선 아이디가 존재하지 않으면 예외를 던진다")
    @Test
    void addSectionFailedByLineNotFound() {
        // given
        given(lineRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        assertThatThrownBy(
                () -> lineService.addSection(1L, new SectionRequest(1L, 1L, 1))
        ).isInstanceOf(LineNotFoundException.class);
    }

    @DisplayName("노선을 추가 요청시 추가할 스테이션 아이디가 존재하지 않으면 예외를 던진다")
    @Test
    void addSectionFailedByStationNotFound() {
        // given
        given(lineRepository.findById(anyLong())).willReturn(Optional.of(new Line()));
        given(stationRepository.findById(any())).willReturn(Optional.empty());
        // when
        assertThatThrownBy(
                () -> lineService.addSection(1L, new SectionRequest(1L, 1L, 1))
        ).isInstanceOf(StationNotFoundException.class);
    }

    @DisplayName("노선을 저장 성공한다")
    @Test
    void saveLineSuccess() {
        // given
        Line line = new Line("신분당선", "#123123", new ArrayList<>());
        given(stationRepository.findById(anyLong())).willReturn(Optional.of(new Station()));
        given(lineRepository.save(any(Line.class))).willReturn(line);

        // when
        LineResponse lineResponse =
                lineService.saveLine(new LineRequest("신분당선", "#123123", 1L, 2L, 3));

        // then
        assertThat(lineResponse).isEqualTo(LineResponse.from(line));
    }

    @DisplayName("노선을 저장 요청시 저장 info 중의 station이 존재하지 않으면 예외를 던진다")
    @Test
    void saveLineFailedByStationNotFound() {
        // given
        given(stationRepository.findById(any())).willReturn(Optional.empty());
        // when
        assertThatThrownBy(
                () -> lineService.saveLine(new LineRequest("신분당선", "#123123", 1L, 2L, 3))
        ).isInstanceOf(StationNotFoundException.class);
    }

    @DisplayName("모든 노선을 보낸다")
    @Test
    void showLines() {
        // given
        Line line1 = new Line("신분당선", "#123123", new ArrayList<>());
        Line line2 = new Line("분당선", "#123124", new ArrayList<>());
        given(lineRepository.findAll()).willReturn(List.of(line1, line2));

        // when
        List<LineResponse> lineResponses = lineService.showLines();

        // then
        assertThat(lineResponses).contains(LineResponse.from(line1), LineResponse.from(line2));
    }

    @DisplayName("아이디로 노선 조회 성공")
    @Test
    void searchByIdSuccess() {
        // given
        Line line = new Line("신분당선", "#123123", new ArrayList<>());
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
        Line line = new Line("신분당선", "#123123", new ArrayList<>());
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
        assertThatThrownBy(() -> lineService.update(1L, new UpdateLineRequest("분당선", "#23232")))
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
        Station gangnamStation = new Station("강남역");
        Station yangjaeStation = new Station("양재역");
        Station pangyoStation = new Station("판교역");
        Line line = new Line();
        Section section = new Section(gangnamStation, yangjaeStation, 10);
        line.addSection(section);
        section = new Section(yangjaeStation, pangyoStation, 10);
        line.addSection(section);
        given(stationRepository.findById(anyLong())).willReturn(Optional.of(pangyoStation));
        given(lineRepository.findById(anyLong())).willReturn(Optional.of(line));

        // when
        lineService.deleteSection(1L, 1L);

        // then
        boolean exists = line.getSections().stream()
                .anyMatch(savedSection -> savedSection.containsStation(pangyoStation));
        assertThat(exists).isFalse();
    }

    @DisplayName("마지막 하행스테이션으로 세션을 삭제 요청시 대상 라인이 존재하지 않으면 예외를 던진다")
    @Test
    void deleteSectionFailedByLineNotExist() {
        // given
        given(stationRepository.findById(anyLong())).willReturn(Optional.of(new Station("강남역")));
        given(lineRepository.findById(anyLong())).willReturn(Optional.empty());

        // when,then
        assertThatThrownBy(() -> lineService.deleteSection(1L,1L)).isInstanceOf(
                LineNotFoundException.class);
    }

    @DisplayName("마지막 하행스테이션으로 세션을 삭제 요청시 대상 스테이션이 존재하지 않으면 예외를 던진다")
    @Test
    void deleteSectionFailedByStationNotExist() {
        // given
        given(stationRepository.findById(anyLong())).willReturn(Optional.of(new Station("강남역")));
        given(lineRepository.findById(anyLong())).willReturn(Optional.empty());

        // when,then
        assertThatThrownBy(() -> lineService.deleteSection(1L,1L)).isInstanceOf(
                LineNotFoundException.class);
    }
}


