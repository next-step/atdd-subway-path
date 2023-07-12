package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    private LineService lineService;

    private Long 강남역 = 1L;
    private Long 양재역 = 2L;
    private Long 다른_지하철역 = 3L;
    private Long 불광역 = 4L;
    private Long 독바위역 = 5L;
    private Long 신분당선 = 1L;

    // given
    @BeforeEach
    void setUp() {
        lenient().when(stationService.findById(강남역)).thenReturn(new Station("강남역"));
        lenient().when(stationService.findById(양재역)).thenReturn(new Station("양재역"));
        lenient().when(stationService.findById(다른_지하철역)).thenReturn(new Station("다른지하철역"));
        lenient().when(stationService.findById(불광역)).thenReturn(new Station("불광역"));
        lenient().when(stationService.findById(독바위역)).thenReturn(new Station("독바위역"));

        lineService = new LineService(lineRepository, stationService);

        Line line = new Line("line", "bg-red-600");
        when(lineRepository.findById(신분당선)).thenReturn(Optional.of(line));
    }

    @Test
    @DisplayName("지하철 노선에 지하철 구간을 등록한다")
    void addSection() {
        // when
        lineService.addSection(신분당선, new SectionRequest(강남역, 양재역, 3));

        // then
        assertThat(lineService.findLineById(신분당선).getSectionsSize()).isEqualTo(1);
    }

    @Test
    @DisplayName("지하철 노선에 새로운역을 상행종점으로 등록한다")
    void addSectionUpStation() {
        // given
        lineService.addSection(신분당선, new SectionRequest(강남역, 양재역, 10));

        // when
        lineService.addSection(신분당선, new SectionRequest(다른_지하철역, 강남역, 15));

        // then
        Assertions.assertThat(lineService.findLineById(신분당선).getSectionsSize()).isEqualTo(2);
    }

    @Test
    @DisplayName("지하철 노선에 새로운역을 하행종점으로 등록한다")
    void addSectionDownStation() {
        // given
        lineService.addSection(신분당선, new SectionRequest(강남역, 양재역, 10));

        // when
        lineService.addSection(신분당선, new SectionRequest(양재역, 다른_지하철역, 5));

        // then
        Assertions.assertThat(lineService.findLineById(신분당선).getSectionsSize()).isEqualTo(2);
    }

    @Test
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되지 않은 구간을 노선에 등록하면 에러가 발생한다")
    void addSectionUpStationOrDownStationNotContain() {
        // given
        lineService.addSection(신분당선, new SectionRequest(강남역, 양재역, 7));

        // when, then
        assertThatThrownBy(() -> lineService.addSection(신분당선, new SectionRequest(불광역
                , 독바위역, 6)))
                .isInstanceOf(DataIntegrityViolationException.class)
                .message().isEqualTo("잘못된 지하철 구간 등록입니다.");
    }

    @Test
    @DisplayName("이미 등록된 상행역과 하행역을 가진 구간을 등록하는 경우 에러가 발생한다")
    void addSectionExistUpStationAndDownStationCaseOne() {
        // given
        lineService.addSection(신분당선, new SectionRequest(강남역, 양재역, 7));

        // when, then
        assertThatThrownBy(() -> lineService.addSection(신분당선
                , new SectionRequest(강남역, 양재역, 5)))
                .isInstanceOf(DataIntegrityViolationException.class)
                .message().isEqualTo("상행역과 하행역이 이미 등록된 구간입니다.");
    }

    @Test
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 구간보다 같거나 큰 경우 에러 발생한다")
    void addSectionDistanceGreaterOrEqual() {
        // when, then
        lineService.addSection(신분당선, new SectionRequest(강남역, 양재역, 10));

        assertThatThrownBy(() -> lineService.addSection(신분당선, new SectionRequest(강남역, 다른_지하철역, 10)))
                .isInstanceOf(DataIntegrityViolationException.class)
                .message().isEqualTo("기존 역 사이 길이보다 크거나 같을 수 없습니다.");

        assertThatThrownBy(() -> lineService.addSection(신분당선, new SectionRequest(강남역, 다른_지하철역, 11)))
                .isInstanceOf(DataIntegrityViolationException.class)
                .message().isEqualTo("기존 역 사이 길이보다 크거나 같을 수 없습니다.");
    }

    @Test
    @DisplayName("지하철 노선의 역 사이에 새로운 역을 등록할 경우 지하철 역목록 조회")
    void getStations() {
        // when
        lineService.addSection(신분당선, new SectionRequest(강남역, 양재역, 14));
        lineService.addSection(신분당선, new SectionRequest(양재역, 다른_지하철역, 10));
        lineService.addSection(신분당선, new SectionRequest(강남역, 독바위역, 10));

        // then
        Assertions.assertThat(lineService.findLineById(신분당선).getStations().stream().map(Station::getName))
                .containsExactly("강남역", "독바위역", "양재역", "다른지하철역");
    }

    @Test
    @DisplayName("지하철 노선에 지하철 구간을 제거한다")
    void deleteSection() {
        // given
        Line line = new Line("line", "bg-red-600");
        Station downStation = new Station("또다른지하철역");
        line.addSection(new Section(line, new Station("지하철역"), downStation, 10));

        long lineId = 1L;
        long downStationId = 2L;
        when(lineRepository.findById(lineId)).thenReturn(Optional.of(line));
        when(stationService.findById(downStationId)).thenReturn(downStation);

        // when
        LineService lineService = new LineService(lineRepository, stationService);
        lineService.deleteSection(lineId, downStationId);

        // then
        Assertions.assertThat(line.getSectionsSize()).isEqualTo(0);
    }
}
