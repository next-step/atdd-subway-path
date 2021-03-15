package nextstep.subway.line.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyByte;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import nextstep.subway.common.exception.InvalidSectionException;
import nextstep.subway.common.exception.NoResourceException;
import nextstep.subway.line.acceptance.LineColor;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("지하철 노선 서비스 테스트")
@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {

  @Mock
  private LineRepository lineRepository;
  @Mock
  private StationService stationService;
  private LineService lineService;

  private Station 광교역;
  private Station 광교중앙역;
  private Station 수지구청역;
  private Station 성복역;
  private Station 상현역;
  private Station 미금역;
  private Station 동천역;
  private Line 신분당선;

  @BeforeEach
  void init() {
    lineService = new LineService(lineRepository, stationService);
    역생성();
    신분당선_생성();
  }

  void 역생성() {
    광교역 = new Station("광교역");
    ReflectionTestUtils.setField(광교역, "id", 1L);

    광교중앙역 = new Station("광교중앙역");
    ReflectionTestUtils.setField(광교중앙역, "id", 2L);

    수지구청역 = new Station("수지구청역");
    ReflectionTestUtils.setField(수지구청역, "id", 4L);

    성복역 = new Station("성복역");
    ReflectionTestUtils.setField(성복역, "id", 5L);

    상현역 = new Station("상현역");
    ReflectionTestUtils.setField(상현역, "id", 6L);

    미금역 = new Station("미금역");
    ReflectionTestUtils.setField(미금역, "id", 7L);

    동천역 = new Station("동천역");
    ReflectionTestUtils.setField(동천역, "id", 8L);
  }

  private void 신분당선_생성() {
    신분당선 = new Line("신분당선", LineColor.RED.toString(), 광교역, 광교중앙역, 5);
    ReflectionTestUtils.setField(신분당선, "id", 1L);
  }

  @DisplayName("지하철 노선을 생성한다")
  @Test
  void saveLine() {
    //given
    given(stationService.findStation(광교역.getId())).willReturn(광교역);
    given(stationService.findStation(광교중앙역.getId())).willReturn(광교중앙역);
    given(lineRepository.save(any(Line.class))).willReturn(신분당선);
    //when
    LineRequest request = new LineRequest("신분당선", LineColor.RED.toString(), 광교역.getId(),
        광교중앙역.getId(), 5);
    LineResponse lineResponse = lineService.saveLine(request);
    //then
    assertThat(lineResponse.getStations()).hasSize(2);

  }

  @DisplayName("지하철 전체노선을 조회한다")
  @Test
  void getLines() {
    //given
    given(lineRepository.findAll()).willReturn(Arrays.asList(신분당선));
    //when
    List<LineResponse> 전체노선목록 = lineService.getLines();
    LineResponse lineResponse = 전체노선목록.stream().findFirst().get();
    //then
    assertAll(
        () -> assertThat(전체노선목록).hasSize(1),
        () -> assertThat(lineResponse.getStations()).extracting(StationResponse::getName)
            .contains("광교역", "광교중앙역")
    );
  }

  @DisplayName("지하철 노선을 조회한다")
  @Test
  void findLineById() {
    //given
    given(lineRepository.findById(any())).willReturn(Optional.of(신분당선));
    //when
    LineResponse lineResponse = lineService.findLine(신분당선.getId());
    //then
    assertAll(
        () -> assertThat(lineResponse.getName()).isEqualTo("신분당선"),
        () -> assertThat(lineResponse.getStations()).extracting(StationResponse::getName)
            .contains("광교역", "광교중앙역")
    );
  }

  @DisplayName("등록되지 않은 지하철 노선이면 NoResourceException throw")
  @Test
  void findLineByIdWithException() {
    //given
    given(lineRepository.findById(any())).willReturn(Optional.empty());
    //then
    assertThrows(NoResourceException.class, () -> lineService.findLine(신분당선.getId()));
  }

  @DisplayName("등록된 지하철 노선을 수정한다")
  @Test
  void modifyLine() {
    //given
    given(lineRepository.findById(any())).willReturn(Optional.of(신분당선));
    //when
    LineRequest 지하철_노선_수정요청 = new LineRequest("신분당선", LineColor.YELLOW.toString(), 광교역.getId(),
        광교중앙역.getId(),
        5);
    LineResponse 지하철_노선_수정응답 = lineService.modifyLine(신분당선.getId(), 지하철_노선_수정요청);
    //then
    assertThat(지하철_노선_수정응답.getColor()).isEqualTo(LineColor.YELLOW.toString());
  }

  @DisplayName("지하철 노선을 제거한다")
  @Test
  void removeLine() {
    //given
    given(stationService.findStation(광교역.getId())).willReturn(광교역);
    given(stationService.findStation(광교중앙역.getId())).willReturn(광교중앙역);
    given(lineRepository.save(any(Line.class))).willReturn(신분당선);
    LineRequest request = new LineRequest("신분당선", LineColor.RED.toString(), 광교역.getId(),
        광교중앙역.getId(), 5);
    lineService.saveLine(request);
    //when
    lineService.removeLine(신분당선.getId());
    //then
    verify(lineRepository, times(1)).deleteById(신분당선.getId());
  }

  @DisplayName("지하철 노선에 새로운 구간을 추가한다")
  @Test
  void addSection() {
    //given
    given(stationService.findStation(광교중앙역.getId())).willReturn(광교중앙역);
    given(stationService.findStation(상현역.getId())).willReturn(상현역);
    given(lineRepository.findById(any())).willReturn(Optional.of(신분당선));
    //when
    lineService.addSection(신분당선.getId(), new SectionRequest(광교중앙역.getId(), 상현역.getId(), 5));
    //then
    assertThat(신분당선.getSections().getSortedStations()).hasSize(3);

  }

  @DisplayName("노선의 구간 중간에 새로운 역을 추가한다.")
  @Test
  void insertSection() {
    //given
    given(stationService.findStation(광교중앙역.getId())).willReturn(광교중앙역);
    given(stationService.findStation(상현역.getId())).willReturn(상현역);
    given(stationService.findStation(성복역.getId())).willReturn(성복역);
    given(lineRepository.findById(any())).willReturn(Optional.of(신분당선));
    lineService.addSection(신분당선.getId(), new SectionRequest(광교중앙역.getId(), 성복역.getId(), 5));
    //when
    lineService.addSection(신분당선.getId(), new SectionRequest(상현역.getId(), 성복역.getId(), 2));
    //then
    assertThat(lineService.findLine(신분당선.getId()).getStations())
        .extracting(StationResponse::getName)
        .containsExactly("광교역", "광교중앙역", "상현역", "성복역");

  }

  @DisplayName("등록하는 구간의 하행역이 노선에 이미 등록되어있으면 구간을 등록할 수 없다.")
  @Test
  void addSectionWithInvalidDownStationSection() {
    //given
    given(stationService.findStation(광교역.getId())).willReturn(광교역);
    given(stationService.findStation(광교중앙역.getId())).willReturn(광교중앙역);
    given(lineRepository.findById(any())).willReturn(Optional.of(신분당선));
    //then
    assertThrows(InvalidSectionException.class,
        () -> lineService
            .addSection(신분당선.getId(), new SectionRequest(광교역.getId(), 광교중앙역.getId(), 5)));
  }

  @DisplayName("노선에서 구간이 1개남아있으면 역을 삭제하지못한다")
  @Test
  void removeSectionIncludedOneSection(){
    //given
    given(lineRepository.findById(any())).willReturn(Optional.of(신분당선));
    //then
    assertThrows(InvalidSectionException.class,
        () -> lineService.removeSection(신분당선.getId(), 광교중앙역.getId()));
  }

  @DisplayName("노선에서 종점이 아닌 중간역을 삭제한다")
  @Test
  void removeSectionWithoutLastStation(){
    //given
    given(lineRepository.findById(any())).willReturn(Optional.of(신분당선));
    given(stationService.findStation(광교역.getId())).willReturn(광교역);
    신분당선.addSection(광교중앙역,상현역,5);
    //when
    lineService.removeSection(신분당선.getId(), 광교역.getId());
    //then
    assertThat(lineService.findLine(신분당선.getId()).getStations())
        .extracting(StationResponse::getName)
        .containsExactly("광교중앙역", "상현역");
  }

  @DisplayName("노선에서 구간을 삭제한다.")
  @Test
  void removeSection(){
    //given
    given(lineRepository.findById(any())).willReturn(Optional.of(신분당선));
    신분당선.addSection(광교중앙역,상현역,5);
    //when
    lineService.removeSection(신분당선.getId(), 상현역.getId());
    //then
    assertThat(신분당선.getSections().getSize()).isEqualTo(1);
  }
}
