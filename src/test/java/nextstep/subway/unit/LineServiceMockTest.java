package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.verify;
import java.util.List;
import java.util.Optional;
import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineSaveRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.LineUpdateRequest;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
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
    private StationService stationService;

    private LineService lineService;

    @BeforeEach
    void setUp() {
        lineService = new LineService(lineRepository, stationService);
    }

    @Test
    @DisplayName("노선 저장")
    void saveLine() {
        //given
        final Line line = new Line(1L, "2호선", "green");
        given(lineRepository.save(any())).willReturn(line);

        역_추가(new Station(1L, "강남역"), new Station(2L, "역삼역"));

        //when
        final LineResponse lineResponse = lineService.saveLine(new LineSaveRequest("2호선", "green", 1L, 2L, 10));

        //then
        assertAll(
            () -> assertThat(lineResponse.getId()).isEqualTo(1L),
            () -> assertThat(lineResponse.getName()).isEqualTo("2호선"),
            () -> assertThat(lineResponse.getColor()).isEqualTo("green"),
            () -> assertThat(lineResponse.getStations().get(0)).isEqualTo(new StationResponse(1L, "강남역")),
            () -> assertThat(lineResponse.getStations().get(1)).isEqualTo(new StationResponse(2L, "역삼역"))
        );
    }

    @Test
    @DisplayName("구간 추가")
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        SectionRequest request = new SectionRequest(1L, 2L, 10);

        노선_조회();

        역_추가(new Station(1L, "강남역"), new Station(2L, "역삼역"));

        // when
        // lineService.addSection 호출
        lineService.addSection(1L, request);

        // then
        // line.findLineById 메서드를 통해 검증
        final LineResponse lineResponse = lineService.findById(1L);
        assertThat(lineResponse.getStations()).hasSize(2);
    }

    @Test
    @DisplayName("전체 노선 조회")
    void showLines() {
        final Line line = new Line(1L, "2호선", "green");
        final Station 강남역 = new Station(1L, "강남역");
        final Station 역삼역 = new Station("역삼역");
        구간_추가(line, 강남역, 역삼역);

        역_응답_생성(강남역, 역삼역);

        given(lineRepository.findAll()).willReturn(
            List.of(line));

        final List<LineResponse> responseList = lineService.showLines();
        assertAll(
            () -> assertThat(responseList).hasSize(1),
            () -> assertThat(responseList.get(0).getName()).isEqualTo("2호선"),
            () -> assertThat(responseList.get(0).getColor()).isEqualTo("green"),
            () -> assertThat(responseList.get(0).getStations()).containsExactly(
                new StationResponse(1L, "강남역"), new StationResponse(2L, "역삼역"))
        );
    }

    @Test
    @DisplayName("노선 정상 조회")
    void findById() {
        //given
        노선_조회();

        //when
        final LineResponse lineResponse = lineService.findById(anyLong());

        //then
        assertThat(lineResponse.getName()).isEqualTo("2호선");
        assertThat(lineResponse.getColor()).isEqualTo("green");
    }

    @Test
    @DisplayName("노선 조회 예외")
    void findByIdException() {
        //when
        given(lineRepository.findById(anyLong())).willThrow(IllegalArgumentException.class);

        //then
        assertThatIllegalArgumentException()
            .isThrownBy(() -> lineService.findById(1L));

        verify(lineRepository, atMostOnce()).findById(anyLong());
    }

    @Test
    @DisplayName("노선 갱신")
    void updateLine() {
        //given
        노선_조회();

        //when
        lineService.updateLine(1L, new LineUpdateRequest("신분당선", "red"));
        final LineResponse lineResponse = lineService.findById(1L);

        //then
        assertThat(lineResponse.getName()).isEqualTo("신분당선");
        assertThat(lineResponse.getColor()).isEqualTo("red");
    }

    @Test
    @DisplayName("노선 삭제")
    void deleteLine() {
        //given
        lineService.deleteLine(1L);

        //when
        verify(lineRepository, atMostOnce()).deleteById(1L);

        //then
        assertThatIllegalArgumentException().isThrownBy(() -> lineService.findById(1L));
    }

    @Test
    @DisplayName("구간 삭제")
    void deleteSection() {
        //given
        Line line = new Line(1L, "2호선", "green");
        final Station 강남역 = new Station(1L, "강남역");
        final Station 역삼역 = new Station(2L, "역삼역");
        final Station 선릉역 = new Station(3L, "선릉역");

        구간_추가(line, 강남역, 역삼역);
        구간_추가(line, 역삼역, 선릉역);
        given(stationService.findById(anyLong())).willReturn(선릉역);
        given(lineRepository.findById(anyLong())).willReturn(Optional.of(line));
        역_응답_생성(강남역, 역삼역);

        //when
        lineService.deleteSection(1L, 3L);

        //then
        final LineResponse lineResponse = lineService.findById(1L);
        assertThat(lineResponse.getStations()).hasSize(2);
    }

    private void 구간_추가(final Line line, final Station upStation, final Station downStation) {
        line.addSections(upStation, downStation, 10);
    }

    private void 노선_조회() {
        Line line = new Line(1L, "2호선", "green");
        given(lineRepository.findById(anyLong())).willReturn(Optional.of(line));
    }

    private void 역_추가(final Station upStation, final Station downStation) {
        given(stationService.findById(upStation.getId())).willReturn(upStation);
        given(stationService.findById(downStation.getId())).willReturn(downStation);

        역_응답_생성(upStation, downStation);
    }

    private void 역_응답_생성(final Station upStation, final Station downStation) {
        given(stationService.createStationResponse(upStation)).willReturn(new StationResponse(1L, "강남역"));
        given(stationService.createStationResponse(downStation)).willReturn(new StationResponse(2L, "역삼역"));
    }

}
