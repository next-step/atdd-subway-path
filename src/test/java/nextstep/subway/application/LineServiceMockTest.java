package nextstep.subway.application;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.UpdateLineRequest;
import nextstep.subway.applicaion.dto.CreateLineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.AddSectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {

    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;
    private LineService lineService;
    private Line 이호선;
    private Line 신분당선;
    private Station 강남역;
    private Station 역삼역;
    private Station 선릉역;

    @BeforeEach
    void setUp() {
        lineService = new LineService(stationService, lineRepository);
        이호선 = new Line(1L, "2호선", "bg-green-600");
        신분당선 = new Line(1L, "신분당성", "bg-red-600");
        강남역 = new Station(1L, "강남역");
        역삼역 = new Station(2L, "역삼역");
        선릉역 = new Station(3L, "선릉역");
    }

    @DisplayName("노선 저장")
    @Test
    void saveLine() {
        // given
        final CreateLineRequest createLineRequest = new CreateLineRequest("2호선", "bg-green-600");
        when(lineRepository.save(any())).thenReturn(이호선);

        // when
        final LineResponse lineResponse = lineService.saveLine(createLineRequest);

        // then
        assertThat(lineResponse.getId()).isNotNull();
    }

    @DisplayName("노선 목록 조회")
    @Test
    void showLines() {
        // given
        when(lineRepository.findAll()).thenReturn(List.of(이호선, 신분당선));

        // when
        final List<LineResponse> lineResponses = lineService.showLines();

        // then
        assertThat(lineResponses).hasSize(2);
    }

    @DisplayName("ID 로 노선 상세 조회")
    @Test
    void findById() {
        // given
        when(lineRepository.findById(any())).thenReturn(Optional.of(이호선));

        // when
        final LineResponse lineResponse = lineService.findById(이호선.getId());

        // then
        assertThat(lineResponse.getId()).isEqualTo(이호선.getId());
    }

    @DisplayName("노선 정보 수정")
    @Test
    void updateLine() {
        // given
        final UpdateLineRequest updateLineRequest = new UpdateLineRequest("신분당선", "bg-red-600");
        when(lineRepository.findById(any())).thenReturn(Optional.of(이호선));

        // when
        lineService.updateLine(이호선.getId(), updateLineRequest);

        // then
        final LineResponse lineResponse = lineService.findById(이호선.getId());
        assertThat(lineResponse.getName()).isEqualTo("신분당선");
        assertThat(lineResponse.getColor()).isEqualTo("bg-red-600");
    }

    @DisplayName("구간 추가")
    @Test
    void addSection() {
        // given
        final AddSectionRequest addSectionRequest = new AddSectionRequest(강남역.getId(), 역삼역.getId(), 10);
        when(stationService.findById(addSectionRequest.getUpStationId())).thenReturn(강남역);
        when(stationService.findById(addSectionRequest.getDownStationId())).thenReturn(역삼역);
        when(lineRepository.findById(any())).thenReturn(Optional.of(이호선));

        // when
        lineService.addSection(이호선.getId(), addSectionRequest);

        // then
        // line.findLineById 메서드를 통해 검증
        // -> lineService.findById 인데 오타인 것이 맞을까요??
        final LineResponse lineResponse = lineService.findById(이호선.getId());
        assertThat(lineResponse.getStations()).hasSize(2);
    }

    @DisplayName("구간 삭제")
    @Test
    void deleteSection() {
        // given
        이호선.addSection(강남역, 역삼역, 10);
        이호선.addSection(역삼역, 선릉역, 10);
        when(lineRepository.findById(any())).thenReturn(Optional.of(이호선));
        when(stationService.findById(any())).thenReturn(선릉역);

        // when
        lineService.deleteSection(이호선.getId(), 선릉역.getId());

        // then
        final LineResponse lineResponse = lineService.findById(이호선.getId());
        assertThat(lineResponse.getStations()).hasSize(2);
    }
}
